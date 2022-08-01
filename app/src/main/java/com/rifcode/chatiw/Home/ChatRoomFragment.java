package com.rifcode.chatiw.Home;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.rifcode.chatiw.R;
import com.rifcode.chatiw.Adapters.RecyclerViewAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatRoomFragment extends Fragment {

    private RecyclerView recFriendsView;
    private DatabaseReference databseRef;
    private DatabaseReference userdatabseRef;
    private FirebaseAuth mAuth;
    public static boolean tv0ChatsBol;
    private String current_user_id;
    private View mainView;


    private ArrayList<Integer> countryImgv = new ArrayList<>();
    private ArrayList<String> countryName = new ArrayList<>();

    public ChatRoomFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_chatroom, container, false);

        initImageBitmaps();
        // Inflate the layout for this fragment
        return  mainView;
    }

    private void initImageBitmaps(){


        countryImgv.add(R.drawable.algeria);
        countryName.add(getString(R.string.algeria));

        countryImgv.add(R.drawable.bahrain);
        countryName.add(getString(R.string.bahr));

        countryImgv.add(R.drawable.comoros);
        countryName.add(getString(R.string.como));

        countryImgv.add(R.drawable.djibouti);
        countryName.add(getString(R.string.dji));

        countryImgv.add(R.drawable.egypt);
        countryName.add(getString(R.string.egy));


        countryImgv.add(R.drawable.iraq);
        countryName.add(getString(R.string.irq));

        countryImgv.add(R.drawable.jordan);
        countryName.add(getString(R.string.jord));

        countryImgv.add(R.drawable.kuwait);
        countryName.add(getString(R.string.kut));

        countryImgv.add(R.drawable.lebanon);
        countryName.add(getString(R.string.leb));

        countryImgv.add(R.drawable.libya);
        countryName.add(getString(R.string.lib));

        countryImgv.add(R.drawable.mauritania);
        countryName.add(getString(R.string.maur));

        countryImgv.add(R.drawable.morocco);
        countryName.add(getString(R.string.mar));

        countryImgv.add(R.drawable.oman);
        countryName.add(getString(R.string.omar));

        countryImgv.add(R.drawable.palestine);
        countryName.add(getString(R.string.pals));

        countryImgv.add(R.drawable.qatar);
        countryName.add(getString(R.string.qatar));

        countryImgv.add(R.drawable.saudi_arabia);
        countryName.add(getString(R.string.saud));

        countryImgv.add(R.drawable.sudan);
        countryName.add(getString(R.string.sud));

        countryImgv.add(R.drawable.syria);
        countryName.add(getString(R.string.syr));

        countryImgv.add(R.drawable.somalia);
        countryName.add(getString(R.string.sola));

        countryImgv.add(R.drawable.tunisia);
        countryName.add(getString(R.string.tun));

        countryImgv.add(R.drawable.united_arab_emirates);
        countryName.add(getString(R.string.uae));

        countryImgv.add(R.drawable.yemen);
        countryName.add(getString(R.string.yem));



        initRecyclerView(mainView);
    }

    private void initRecyclerView(View view){
        GridLayoutManager gridLayoutManager;
        RecyclerView recyclerView = view.findViewById(R.id.recyclerv_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getActivity(), countryName, countryImgv);
        recyclerView.setAdapter(adapter);

        recyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getActivity(),3
                ,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

}
