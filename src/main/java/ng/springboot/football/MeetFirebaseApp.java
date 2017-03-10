package ng.springboot.football;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MeetFirebaseApp {

    public void run() {
        // login
        InputStream serviceAccount = getClass().getResourceAsStream("serviceAccountKey.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
            .setDatabaseUrl("https://meet-spring-boot.firebaseio.com")
            .build();
        FirebaseApp.initializeApp(options);
        // listeners
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        listenResults(database);
    }

    private void listenResults(FirebaseDatabase database) {
        DatabaseReference resultsRef = database.getReference("/server/results");

        resultsRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousKey) {
                    handleNewResult(database, dataSnapshot);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousKey) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousKey) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
    }

    private void handleNewResult(FirebaseDatabase database, DataSnapshot dataSnapshot) {
        EventResult eventResult = dataSnapshot.getValue(EventResult.class);
        String eventResultKey = dataSnapshot.getKey();
        System.out.println("result: " + eventResult);
        DatabaseReference betsRef = database.getReference("/public/bets").child(dataSnapshot.getKey());
        Query winners = betsRef.orderByChild("prediction").equalTo(eventResult.getResult());
        winners.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    handleWinners(database, dataSnapshot, eventResult, eventResultKey);
                }

                @Override
                    public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
    }

    private void handleWinners(FirebaseDatabase database, DataSnapshot dataSnapshot,
                               EventResult eventResult, String eventResultKey) {
        Map<String, BetWinner> winners = new HashMap<String, BetWinner>();
        dataSnapshot.getChildren().forEach(c -> {
                Bet bet = c.getValue(Bet.class);
                System.out.println("bet: " + bet);
                winners.put(c.getKey(), new BetWinner(bet.getName(), bet.getEmail()));
            });

        Map<String, Object> winnersUpdates = new HashMap<String, Object>();
        winnersUpdates.put("/users/" + eventResult.getOwner() + "/events/" + eventResultKey + "/status", "finished");
        winnersUpdates.put("/users/" + eventResult.getOwner() + "/events/" + eventResultKey + "/winners", dataSnapshot.getChildrenCount());
        winnersUpdates.put("/users/" + eventResult.getOwner() + "/events/" + eventResultKey + "/resultA", eventResult.getResultA());
        winnersUpdates.put("/users/" + eventResult.getOwner() + "/events/" + eventResultKey + "/resultB", eventResult.getResultB());
        winnersUpdates.put("/public/events/" + eventResultKey + "/status", "finished");
        winnersUpdates.put("/public/events/" + eventResultKey + "/winners", dataSnapshot.getChildrenCount());
        winnersUpdates.put("/public/events/" + eventResultKey + "/resultA", eventResult.getResultA());
        winnersUpdates.put("/public/events/" + eventResultKey + "/resultB", eventResult.getResultB());
        winnersUpdates.put("/public/winners/" + eventResultKey, winners);
        winnersUpdates.put("/server/results/" + eventResultKey, null);
        database.getReference().updateChildren(winnersUpdates);
    }

}
