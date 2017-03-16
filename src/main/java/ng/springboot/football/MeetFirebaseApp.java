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
                public void onChildAdded(DataSnapshot snapshot, String previousKey) {
                    handleNewResult(database, snapshot);
                }

                @Override
                public void onChildChanged(DataSnapshot snapshot, String previousKey) {

                }

                @Override
                public void onChildMoved(DataSnapshot snapshot, String previousKey) {

                }

                @Override
                public void onChildRemoved(DataSnapshot snapshot) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
    }

    private void handleNewResult(FirebaseDatabase database, DataSnapshot snapshot) {
        EventResult result = snapshot.getValue(EventResult.class);
        String resultKey = snapshot.getKey();
        System.out.println("result: " + result);
        DatabaseReference betsRef = database.getReference("/public/bets").child(snapshot.getKey());
        Query winners = betsRef.orderByChild("prediction").equalTo(result.getResult());
        winners.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    handleWinners(database, snapshot, result, resultKey);
                }

                @Override
                    public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
    }

    private void handleWinners(FirebaseDatabase database, DataSnapshot snapshot, EventResult result, String resultKey) {
        Map<String, BetWinner> winners = new HashMap<String, BetWinner>();
        snapshot.getChildren().forEach(c -> {
                Bet bet = c.getValue(Bet.class);
                System.out.println("bet: " + bet);
                winners.put(c.getKey(), new BetWinner(bet.getName(), bet.getEmail()));
            });
        Map<String, Object> winnersUpdates = new HashMap<String, Object>();

        String userEventPrefix = "/users/" + result.getOwner() + "/events/" + resultKey;
        winnersUpdates.put(userEventPrefix + "/status", "finished");
        winnersUpdates.put(userEventPrefix + "/winners", snapshot.getChildrenCount());
        winnersUpdates.put(userEventPrefix + "/resultA", result.getResultA());
        winnersUpdates.put(userEventPrefix + "/resultB", result.getResultB());

        String publicEventPrefix = "/public/events/" + resultKey;
        winnersUpdates.put(publicEventPrefix + "/status", "finished");
        winnersUpdates.put(publicEventPrefix + "/winners", snapshot.getChildrenCount());
        winnersUpdates.put(publicEventPrefix + "/resultA", result.getResultA());
        winnersUpdates.put(publicEventPrefix + "/resultB", result.getResultB());

        winnersUpdates.put("/public/winners/" + resultKey, winners);

        winnersUpdates.put("/server/results/" + resultKey, null);

        database.getReference().updateChildren(winnersUpdates);
    }
}
