package ng.springboot.football;

import java.io.InputStream;

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
        InputStream serviceAccount = getClass().getResourceAsStream("serviceAccountKey.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
            .setDatabaseUrl("https://meet-spring-boot.firebaseio.com")
            .build();

        FirebaseApp.initializeApp(options);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference resultsRef = database.getReference("/results");

        resultsRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousKey) {
                    EventResult eventResult = dataSnapshot.getValue(EventResult.class);
                    System.out.println(eventResult);
                    System.out.println(dataSnapshot.getKey());

                    DatabaseReference betsRef = database.getReference("/bets").child(dataSnapshot.getKey());
                    Query winners = betsRef.orderByChild("prediction").equalTo(eventResult.getResult());
                    winners.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                System.out.println("Winners: " + dataSnapshot.getChildrenCount());
                                dataSnapshot.getChildren().forEach(c -> System.out.println("winner: " + c.getKey()));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                System.out.println("The read failed: " + databaseError.getCode());
                            }
                        });
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

    public static class EventResult {

        private int resultA;
        private int resultB;
        private String result;

        private EventResult() {

        }

        public EventResult(int resultA, int resultB, String result) {
            this.resultA = resultA;
            this.resultB = resultB;
            this.result = result;
        }

        public int getResultA() {
            return resultA;
        }

        public int getResultB() {
            return resultB;
        }

        public String getResult() {
            return result;
        }

        @Override
        public String toString() {
            return "ng.springboot.football.EventResult { teamA: \"" + resultA + "\""
                + ", teamB: \"" + resultB + "\""
                + ", team: \"" + result + "\" }";
        }

    }

}
