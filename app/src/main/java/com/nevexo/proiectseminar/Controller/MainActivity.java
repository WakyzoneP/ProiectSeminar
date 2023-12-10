package com.nevexo.proiectseminar.Controller;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nevexo.proiectseminar.Adaptors.CustomMasinaAdapter;
import com.nevexo.proiectseminar.Model.Masina;
import com.nevexo.proiectseminar.Network.ExtractJSON;
import com.nevexo.proiectseminar.Network.ExtractXML;
import com.nevexo.proiectseminar.R;
import com.nevexo.proiectseminar.ViewFirebase;
import com.nevexo.proiectseminar.database.MasinaDB;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton floatingActionButton;
    private Intent intent;
    private ListView listViewMasini;
    private List<Masina> listMasini = new ArrayList<Masina>();
    public static final int REQUEST_CODE_ADD = 100;
    public static final int REQUEST_CODE_EDIT = 101;

    private int poz;
    public static final String EDIT_MASINA = "editMasina";

    private ActivityResultLauncher<Intent> addActivityResultLauncher;
    private ActivityResultLauncher<Intent> editActivityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        listViewMasini = (ListView) findViewById(R.id.listViewMasini);
        floatingActionButton.setOnClickListener(this);

        addActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                addMasinaCallback
        );

        editActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                editMasinaCallback
        );


        listViewMasini.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Masina masina = listMasini.get(i);
                ArrayAdapter arrayAdapter = (ArrayAdapter) listViewMasini.getAdapter();

                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Confirmare stergere")
                        .setMessage("Sigur doriti stergerea?")
                        .setNegativeButton("Nu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MainActivity.this, "Nu am sters nimic!", Toast.LENGTH_SHORT).show();
                                dialogInterface.cancel();
                            }
                        })
                        .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                listMasini.remove(masina);

                                MasinaDB masinaDB = MasinaDB.getInstance(MainActivity.this);
                                masinaDB.getMasinaDAO().delete(masina);

                                arrayAdapter.notifyDataSetChanged();
                                Toast.makeText(MainActivity.this, "Am sters!", Toast.LENGTH_SHORT).show();
                                dialogInterface.cancel();
                            }
                        }).create();

                dialog.show();
                return true;
            }
        });

        listViewMasini.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                poz = i;
                intent = new Intent(MainActivity.this, AddActivity.class);
                intent.putExtra(EDIT_MASINA, listMasini.get(i));
                editActivityResultLauncher.launch(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        MasinaDB masinaDB = MasinaDB.getInstance(this);
        listMasini = masinaDB.getMasinaDAO().getAll();

        CustomMasinaAdapter customAdapter = new CustomMasinaAdapter(MainActivity.this, R.layout.elem_listview, listMasini, getLayoutInflater()) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                Masina localMasina = listMasini.get(position);

                TextView textViewPret = (TextView) view.findViewById(R.id.TextViewPret);
                if (localMasina.getPret() > 10000) {
                    textViewPret.setTextColor(Color.RED);
                } else {
                    textViewPret.setTextColor(Color.GREEN);
                }

                TextView textViewMotorizare = (TextView) view.findViewById(R.id.TextViewMotorizare);
                if (localMasina.getMotorizare().equals("Electric"))
                    textViewMotorizare.setTextColor(Color.BLUE);
                else if (localMasina.getMotorizare().equals("Hibrid"))
                    textViewMotorizare.setTextColor(Color.YELLOW);
                else
                    textViewMotorizare.setTextColor(Color.rgb(255, 165, 0));

                return view;
            }
        };

        listViewMasini.setAdapter(customAdapter);
    }

    private ActivityResultCallback<ActivityResult> editMasinaCallback = new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Masina masina = (Masina) result.getData().getSerializableExtra(AddActivity.ADD_MASINA);
                if (masina != null) {
                    listMasini.get(poz).setMarca(masina.getMarca());
                    listMasini.get(poz).setDataFabricatie(masina.getDataFabricatie());
                    listMasini.get(poz).setPret(masina.getPret());
                    listMasini.get(poz).setCuloare(masina.getCuloare());
                    listMasini.get(poz).setMotorizare(masina.getMotorizare());

                    MasinaDB masinaDB = MasinaDB.getInstance(MainActivity.this);
                    masinaDB.getMasinaDAO().update(listMasini.get(poz));

                    CustomMasinaAdapter customAdapter = new CustomMasinaAdapter(MainActivity.this, R.layout.elem_listview, listMasini, getLayoutInflater()) {
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);

                            Masina localMasina = listMasini.get(position);

                            TextView textViewPret = (TextView) view.findViewById(R.id.TextViewPret);
                            if (localMasina.getPret() > 10000) {
                                textViewPret.setTextColor(Color.RED);
                            } else {
                                textViewPret.setTextColor(Color.GREEN);
                            }

                            TextView textViewMotorizare = (TextView) view.findViewById(R.id.TextViewMotorizare);
                            if (localMasina.getMotorizare().equals("Electric"))
                                textViewMotorizare.setTextColor(Color.BLUE);
                            else if (localMasina.getMotorizare().equals("Hibrid"))
                                textViewMotorizare.setTextColor(Color.YELLOW);
                            else
                                textViewMotorizare.setTextColor(Color.rgb(255, 165, 0));

                            return view;
                        }
                    };
                    listViewMasini.setAdapter(customAdapter);
                }
            }
        }
    };

    private ActivityResultCallback<ActivityResult> addMasinaCallback = new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Masina masina = (Masina) result.getData().getSerializableExtra(AddActivity.ADD_MASINA);
                if (masina != null) {
//                    Toast.makeText(this, masina.toString(), Toast.LENGTH_SHORT).show();
                    listMasini.add(masina);
                    CustomMasinaAdapter customAdapter = new CustomMasinaAdapter(MainActivity.this, R.layout.elem_listview, listMasini, getLayoutInflater()) {
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);

                            Masina localMasina = listMasini.get(position);

                            TextView textViewPret = (TextView) view.findViewById(R.id.TextViewPret);
                            if (localMasina.getPret() > 10000) {
                                textViewPret.setTextColor(Color.RED);
                            } else {
                                textViewPret.setTextColor(Color.GREEN);
                            }

                            TextView textViewMotorizare = (TextView) view.findViewById(R.id.TextViewMotorizare);
                            if (localMasina.getMotorizare().equals("Electric"))
                                textViewMotorizare.setTextColor(Color.BLUE);
                            else if (localMasina.getMotorizare().equals("Hibrid"))
                                textViewMotorizare.setTextColor(Color.YELLOW);
                            else
                                textViewMotorizare.setTextColor(Color.rgb(255, 165, 0));

                            return view;
                        }
                    };

                    MasinaDB masinaDB = MasinaDB.getInstance(MainActivity.this);
                    masinaDB.getMasinaDAO().insert(masina);

                    listViewMasini.setAdapter(customAdapter);
                }
            }
        }
    };

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.floatingActionButton) {
            intent = new Intent(this, AddActivity.class);
            addActivityResultLauncher.launch(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD && resultCode == RESULT_OK && data != null) {
            Masina masina = (Masina) data.getSerializableExtra(AddActivity.ADD_MASINA);
            if (masina != null) {
                Toast.makeText(this, masina.toString(), Toast.LENGTH_SHORT).show();
                listMasini.add(masina);
                CustomMasinaAdapter customAdapter = new CustomMasinaAdapter(this, R.layout.elem_listview, listMasini, getLayoutInflater()) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);

                        Masina localMasina = listMasini.get(position);

                        TextView textViewPret = (TextView) view.findViewById(R.id.TextViewPret);
                        if (localMasina.getPret() > 10000) {
                            textViewPret.setTextColor(Color.RED);
                        } else {
                            textViewPret.setTextColor(Color.GREEN);
                        }

                        TextView textViewMotorizare = (TextView) view.findViewById(R.id.TextViewMotorizare);
                        if (localMasina.getMotorizare().equals("Electric"))
                            textViewMotorizare.setTextColor(Color.BLUE);
                        else if (localMasina.getMotorizare().equals("Hibrid"))
                            textViewMotorizare.setTextColor(Color.YELLOW);
                        else
                            textViewMotorizare.setTextColor(Color.rgb(255, 165, 0));

                        return view;
                    }
                };
                listViewMasini.setAdapter(customAdapter);
            }
        }
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK && data != null) {
            Masina masina = (Masina) data.getSerializableExtra(AddActivity.ADD_MASINA);
            if (masina != null) {
                listMasini.get(poz).setMarca(masina.getMarca());
                listMasini.get(poz).setDataFabricatie(masina.getDataFabricatie());
                listMasini.get(poz).setPret(masina.getPret());
                listMasini.get(poz).setCuloare(masina.getCuloare());
                listMasini.get(poz).setMotorizare(masina.getMotorizare());

                CustomMasinaAdapter customAdapter = new CustomMasinaAdapter(this, R.layout.elem_listview, listMasini, getLayoutInflater());
                listViewMasini.setAdapter(customAdapter);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.option1) {
            Intent intent1 = new Intent(this, BNRActivity.class);
            startActivity(intent1);
            return true;
        } else if (item.getItemId() == R.id.option2) {
            ExtractXML extractXML = new ExtractXML() {
                @Override
                protected void onPostExecute(InputStream inputStream) {
                    listMasini.addAll(this.masini);

                    MasinaDB masinaDB = MasinaDB.getInstance(MainActivity.this);
                    masinaDB.getMasinaDAO().insertAll(this.masini);

                    CustomMasinaAdapter customAdapter = new CustomMasinaAdapter(MainActivity.this, R.layout.elem_listview, listMasini, getLayoutInflater()) {
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);

                            Masina localMasina = listMasini.get(position);

                            TextView textViewPret = (TextView) view.findViewById(R.id.TextViewPret);
                            if (localMasina.getPret() > 10000) {
                                textViewPret.setTextColor(Color.RED);
                            } else {
                                textViewPret.setTextColor(Color.GREEN);
                            }

                            TextView textViewMotorizare = (TextView) view.findViewById(R.id.TextViewMotorizare);
                            if (localMasina.getMotorizare().equals("Electric"))
                                textViewMotorizare.setTextColor(Color.BLUE);
                            else if (localMasina.getMotorizare().equals("Hibrid"))
                                textViewMotorizare.setTextColor(Color.YELLOW);
                            else
                                textViewMotorizare.setTextColor(Color.rgb(255, 165, 0));

                            return view;
                        }
                    };

                    listViewMasini.setAdapter(customAdapter);
                }
            };
            try {
                extractXML.execute(new URL("https://pastebin.com/raw/K2gc0QSx"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return true;
        } else if (item.getItemId() == R.id.option3) {
            ExtractJSON extractJSON = new ExtractJSON() {

                ProgressDialog progressDialog;

                @Override
                protected void onPreExecute() {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setTitle("Loading...");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }

                @Override
                protected void onPostExecute(InputStream inputStream) {
                    progressDialog.dismiss();
                    listMasini.addAll(this.masini);

                    MasinaDB masinaDB = MasinaDB.getInstance(MainActivity.this);
                    masinaDB.getMasinaDAO().insertAll(this.masini);

                    CustomMasinaAdapter customAdapter = new CustomMasinaAdapter(MainActivity.this, R.layout.elem_listview, listMasini, getLayoutInflater()) {
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);

                            Masina localMasina = listMasini.get(position);

                            TextView textViewPret = (TextView) view.findViewById(R.id.TextViewPret);
                            if (localMasina.getPret() > 10000) {
                                textViewPret.setTextColor(Color.RED);
                            } else {
                                textViewPret.setTextColor(Color.GREEN);
                            }

                            TextView textViewMotorizare = (TextView) view.findViewById(R.id.TextViewMotorizare);
                            if (localMasina.getMotorizare().equals("Electric"))
                                textViewMotorizare.setTextColor(Color.BLUE);
                            else if (localMasina.getMotorizare().equals("Hibrid"))
                                textViewMotorizare.setTextColor(Color.YELLOW);
                            else
                                textViewMotorizare.setTextColor(Color.rgb(255, 165, 0));

                            return view;
                        }
                    };

                    listViewMasini.setAdapter(customAdapter);
                }
            };
            try {
                extractJSON.execute(new URL("https://pastebin.com/raw/r9Yka6fJ"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return true;
        } else if (item.getItemId() == R.id.option4) {
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://bv-seminar-default-rtdb.firebaseio.com/");
            DatabaseReference myRef = database.getReference("bv-seminar-default-rtdb");
            myRef.keepSynced(true);

            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        for(DataSnapshot masinaSnapshot : snapshot.getChildren()) {
                            Masina masina = masinaSnapshot.getValue(Masina.class);
                            listMasini.add(masina);
                        }

                    }
                    CustomMasinaAdapter customAdapter = new CustomMasinaAdapter(MainActivity.this, R.layout.elem_listview, listMasini, getLayoutInflater()) {
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);

                            Masina localMasina = listMasini.get(position);

                            TextView textViewPret = (TextView) view.findViewById(R.id.TextViewPret);
                            if (localMasina.getPret() > 10000) {
                                textViewPret.setTextColor(Color.RED);
                            } else {
                                textViewPret.setTextColor(Color.GREEN);
                            }

                            TextView textViewMotorizare = (TextView) view.findViewById(R.id.TextViewMotorizare);
                            if (localMasina.getMotorizare().equals("Electric"))
                                textViewMotorizare.setTextColor(Color.BLUE);
                            else if (localMasina.getMotorizare().equals("Hibrid"))
                                textViewMotorizare.setTextColor(Color.YELLOW);
                            else
                                textViewMotorizare.setTextColor(Color.rgb(255, 165, 0));

                            return view;
                        }
                    };

                    listViewMasini.setAdapter(customAdapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

            myRef.child("bv-seminar-default-rtdb").addListenerForSingleValueEvent(listener);
            return true;
        } else if (item.getItemId() == R.id.option5) {
            Intent intent = new Intent(this, ViewFirebase.class);
            startActivity(intent);

            return true;
        }
        return false;
    }
}
