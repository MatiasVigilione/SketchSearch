package core.tfg.sketchsearch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class Configuration extends AppCompatActivity {

    private int itemSelected = 0;
    private boolean window_state = false;

    int orb_dis, bins_r, bins_theta, simp_shapeContext, simp_hausdorff, qlen, window_mov, window_height, window_width;
    Double r_in, r_out;
    boolean win_state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);

        final GlobalVariables globalVariables = (GlobalVariables) getApplicationContext();

        Toolbar mtToolbar = (Toolbar) findViewById(R.id.toolbar);
        getSupportActionBar();

        final Spinner comparisonType = (Spinner) findViewById(R.id.comparison_type);
        final LinearLayout ORB = (LinearLayout) findViewById(R.id.ORB);
        final LinearLayout ShapeContext = (LinearLayout) findViewById(R.id.ShapeContext);
        final LinearLayout Hausdorff_distance = (LinearLayout) findViewById(R.id.Hausdorff_distance);
        final Spinner searchWindow = (Spinner) findViewById(R.id.search_window);
        final LinearLayout window = (LinearLayout) findViewById(R.id.Window);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.comparison_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comparisonType.setAdapter(adapter);

        String kind = globalVariables.getKind();
        boolean win = globalVariables.isWindow_state();
        switch (kind) {
            case ("ORB"):
                comparisonType.setSelection(0);
                break;
            case ("ShapeContext"):
                comparisonType.setSelection(1);
                break;
            case ("Hausdorff"):
                comparisonType.setSelection(2);
                break;
        }

        comparisonType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getSelectedItemPosition()){
                    case (0):
                        itemSelected=0;
                        ORB.setVisibility(View.VISIBLE);
                        ShapeContext.setVisibility(View.GONE);
                        Hausdorff_distance.setVisibility(View.GONE);
                        break;
                    case (1):
                        itemSelected=1;
                        ORB.setVisibility(View.GONE);
                        ShapeContext.setVisibility(View.VISIBLE);
                        Hausdorff_distance.setVisibility(View.GONE);
                        break;
                    case (2):
                        itemSelected=2;
                        ORB.setVisibility(View.GONE);
                        ShapeContext.setVisibility(View.GONE);
                        Hausdorff_distance.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }

        });

        ArrayAdapter<CharSequence> win_adapter = ArrayAdapter.createFromResource(this,
                R.array.window, android.R.layout.simple_spinner_item);
        win_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchWindow.setAdapter(win_adapter);

        if (win) {
            searchWindow.setSelection(1);
        } else {
            searchWindow.setSelection(0);
        }

        searchWindow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getSelectedItemPosition()){
                    case (0):
                        window_state=false;
                        window.setVisibility(View.GONE);
                        break;
                    case (1):
                        window_state=true;
                        window.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

        final EditText ORBdistance = (EditText) findViewById(R.id.ORB_distance);
        ORBdistance.setHint(String.valueOf(globalVariables.getORB_distance()));

        final EditText nbins_r = (EditText) findViewById(R.id.nbins_r);
        nbins_r.setHint(String.valueOf(globalVariables.getNbins_r()));
        final EditText nbins_theta = (EditText) findViewById(R.id.nbins_theta);
        nbins_theta.setHint(String.valueOf(globalVariables.getNbins_theta()));
        final EditText r_inner = (EditText) findViewById(R.id.r_inner);
        r_inner.setHint(globalVariables.getR_innter().toString());
        final EditText r_outter = (EditText) findViewById(R.id.r_outter);
        r_outter.setHint(globalVariables.getR_outter().toString());
        final EditText simpleto_shapeContext = (EditText) findViewById(R.id.simpleto_shapeContext);
        simpleto_shapeContext.setHint(String.valueOf(globalVariables.getSimpleto_shapeContext()));
        final EditText qlength = (EditText) findViewById(R.id.qlenght);
        qlength.setHint(String.valueOf(globalVariables.getQlength()));

        final EditText simpleto_hausdorff = (EditText) findViewById(R.id.simpleto_hausdorff);
        simpleto_hausdorff.setHint(String.valueOf(globalVariables.getSimpleto_hausdorff()));

        final EditText win_mov = (EditText) findViewById(R.id.win_mov);
        win_mov.setHint(String.valueOf(globalVariables.getWindow_mov()));
        final EditText win_height = (EditText) findViewById(R.id.win_height);
        win_height.setHint(String.valueOf(globalVariables.getWindow_height()));
        final EditText win_width = (EditText) findViewById(R.id.win_width);
        win_width.setHint(String.valueOf(globalVariables.getWindow_width()));

        final Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (itemSelected){
                    case (0):
                        globalVariables.setKind("ORB");
                        if (!ORBdistance.getText().toString().matches("")) {
                            orb_dis = Integer.valueOf(ORBdistance.getText().toString());
                            globalVariables.setORB_distance(orb_dis);
                        }
                        comparisonType.setSelection(0);
                        break;
                    case(1):
                        globalVariables.setKind("ShapeContext");
                        comparisonType.setSelection(1);
                        if (!nbins_r.getText().toString().matches("")) {
                            bins_r = Integer.valueOf(nbins_r.getText().toString());
                            globalVariables.setNbins_r(bins_r);
                        }
                        if (!nbins_theta.getText().toString().matches("")) {
                            bins_theta = Integer.valueOf(nbins_theta.getText().toString());
                            globalVariables.setNbins_theta(bins_theta);
                        }
                        if (!r_inner.getText().toString().matches("")) {
                            r_in = Double.valueOf(r_inner.getText().toString());
                            globalVariables.setR_innter(r_in);
                        }
                        if (!r_outter.getText().toString().matches("")) {
                            r_out = Double.valueOf(r_outter.getText().toString());
                            globalVariables.setR_outter(r_out);
                        }
                        if (!simpleto_shapeContext.getText().toString().matches("")) {
                            simp_shapeContext = Integer.valueOf(simpleto_shapeContext.getText().toString());
                            globalVariables.setSimpleto_shapeContext(simp_shapeContext);
                        }
                        if (!qlength.getText().toString().matches("")) {
                            qlen = Integer.valueOf(qlength.getText().toString());
                            globalVariables.setQlength(qlen);
                        }
                        break;
                    case (2):
                        globalVariables.setKind("Hausdorff");
                        if (!simpleto_hausdorff.getText().toString().matches("")) {
                            simp_hausdorff = Integer.valueOf(simpleto_hausdorff.getText().toString());
                            globalVariables.setSimpleto_hausdorff(simp_hausdorff);
                        }
                        break;
                }
                if (win_state) {
                    globalVariables.setWindow_state(window_state);
                    if (!win_mov.getText().toString().matches("")) {
                        window_mov = Integer.valueOf(win_mov.getText().toString());
                        globalVariables.setWindow_mov(window_mov);
                    }
                    if (!win_height.getText().toString().matches("")) {
                        window_height = Integer.valueOf(win_height.getText().toString());
                        globalVariables.setWindow_height(window_height);
                    }
                    if (!win_width.getText().toString().matches("")) {
                        window_width = Integer.valueOf(win_width.getText().toString());
                        globalVariables.setWindow_width(window_width);
                    }
                } else {
                    globalVariables.setWindow_state(window_state);
                }
                Toast.makeText(getApplicationContext(), "Configuration saved", Toast.LENGTH_SHORT).show();
                finish();

            }
        });

    }
}
