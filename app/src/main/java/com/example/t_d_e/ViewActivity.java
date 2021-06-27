package com.example.t_d_e;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        RecyclerView jobCycler = findViewById(R.id.jobCycler);
        jobCycler.setLayoutManager(new LinearLayoutManager(this));
        List<Object> data = new ArrayList<>();
        JobAdapter adapter = new JobAdapter(this, R.layout.job_layout, data);
        jobCycler.setAdapter(adapter);
        FirebaseFirestore.getInstance()
                .collection("delivery")
                .whereEqualTo("senderid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    data.addAll(queryDocumentSnapshots.getDocuments());
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "data loaded", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "data could not load", Toast.LENGTH_SHORT).show();
                });

    }

    private class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {

        private final LayoutInflater inflater;
        private final int job_layout;
        private final List<Object> objectList;
        private final Context context;

        public JobAdapter(Context context, int job_layout, List<Object> objectList) {
            inflater = LayoutInflater.from(context);
            this.job_layout = job_layout;
            this.objectList = objectList;
            this.context = context;
        }

        @NonNull
        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            return new ViewHolder(inflater.inflate(job_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull ViewActivity.JobAdapter.ViewHolder holder, int position) {
            DocumentSnapshot o = (DocumentSnapshot) objectList.get(position);
            String receiver_name = o.getString("Receiver Name");
            String receiver_mobile_number = o.getString("Receiver Mobile Number");
            String sender_number = o.getString("Sender Number");
            String senderid = o.getString("senderid");
            String pick_up_instruction = o.getString("Pick up Instruction");
            String delivery_instruction = o.getString("Delivery Instruction");
            String package_size = o.getString("Package Size");
            String package_weight = o.getString("Package Weight");
            Double delivery_price = o.getDouble("Delivery Price");
            String delivery_vehicle = o.getString("Delivery Vehicle");
            String dp = o.getString("dp");
            String statue = o.getString("statue");
            Double mylatitude = o.getDouble("mylatitude");
            Double mylongitude = o.getDouble("mylongitude");
            Double destlatitude = o.getDouble("destlatitude");
            Double destlongitude = o.getDouble("destlongitude");
            String destaddr = o.getString("destaddr");
            String myaddr = o.getString("myaddr");
            holder.sent.setText(String.format("%s \n%s", receiver_name, receiver_mobile_number));
            holder.details.setText(
                    String.format("destination %s\npickup %s\nstatus %s\ndelivery person %s\ndelivery vehicle %s\npackage size %s\npackage weight %s\npackage price %s\n", destaddr, myaddr, statue, dp, delivery_vehicle, package_size, package_weight, delivery_price)
            );
            holder.name.setText(String.format("delivery %s package", receiver_name));

        }

        @Override
        public int getItemCount() {
            return objectList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView name, sent, details;

            public ViewHolder(@NonNull @NotNull View v) {
                super(v);
                name = v.findViewById(R.id.name);
                sent = v.findViewById(R.id.sent);
                details = v.findViewById(R.id.details);
                v.setOnClickListener(view -> {
                    DocumentSnapshot o = (DocumentSnapshot) objectList.get(getAdapterPosition());
                    Toast.makeText(ViewActivity.this, o.getDouble("Delivery Price") + "", Toast.LENGTH_SHORT).show();
                    String dphonenumber = o.getString("dphonenumber");
                    if(dphonenumber!=null) {
                        new AlertDialog.Builder(context)
                                .setTitle("call delivery person")
                                .setMessage("call " + dphonenumber + "\n" + "Status " + o.getString("statue"))
                                .setPositiveButton("ok", (dialogInterface, i) -> {

                                })
                                .create()
                                .show();
                    }else{
                        new AlertDialog.Builder(context)
                                .setTitle("No one accepted job")
                                .setMessage("job not picked up yet!")
                                .setPositiveButton("ok", (dialogInterface, i) -> {

                                })
                                .create()
                                .show();
                    }
                });
            }
        }
    }
}