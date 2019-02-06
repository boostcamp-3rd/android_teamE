package com.teame.boostcamp.myapplication.ui.Search;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.teame.boostcamp.myapplication.R;
import com.teame.boostcamp.myapplication.adapter.SearchAdapter.ExListAdapter;
import com.teame.boostcamp.myapplication.databinding.FragmentSearchBinding;
import com.teame.boostcamp.myapplication.ui.base.BaseFragment;
import com.teame.boostcamp.myapplication.util.InputKeyboardUtil;
import com.teame.boostcamp.myapplication.util.ResourceProvider;
import com.teame.boostcamp.myapplication.util.TedPermissionUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.reactivex.disposables.Disposable;

public class SearchFragment extends BaseFragment<FragmentSearchBinding, SearchContract.Presenter> implements OnMapReadyCallback, SearchContract.View {

    private GoogleMap googleMap=null;
    private static final float ZOOM=16;
    private Disposable disposable;
    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_search;
    }

    @Override
    protected SearchContract.Presenter getPresenter() {
        return presenter;
    }

    @Deprecated
    public SearchFragment() {
        // 기본 생성자는 쓰지 말것 (new Instance 사용)
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=super.onCreateView(inflater,container,savedInstanceState);
        setPresenter(new SearchPresenter(this, new ResourceProvider(getContext())));
        setUp();
        return view;
    }

    private void setUp(){
        //adapter setting
        ExListAdapter adapter=new ExListAdapter();
        adapter.setOnItemClickListener(text -> {
            presenter.onSearchSubmit(text);
        });

        //RecyclerView setting
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        DividerItemDecoration divider=new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        binding.rvExList.addItemDecoration(divider);
        binding.rvExList.setLayoutManager(layoutManager);
        binding.rvExList.setAdapter(adapter);
        binding.rvExList.setOnTouchListener((v, event) -> {
            if(event.getAction()==MotionEvent.ACTION_DOWN){
                InputKeyboardUtil.hideKeyboard(getActivity());
                return true;
            }
            return false;
        });

        //presenter setting
        presenter.setAdpaterView(adapter);
        presenter.setAdpaterModel(adapter);

        //googlemap setting
        binding.mvGooglemap.getMapAsync(this);

        //SearchBar setting
        binding.toolbarSearch.setOnClickListener(__ -> {
            binding.svPlace.setIconified(false);
            showExSearchView();
        });
        binding.svPlace.setMaxWidth(binding.toolbarSearch.getWidth());
        binding.svPlace.setOnQueryTextFocusChangeListener((__, hasFocus) -> {
            if(hasFocus) {
                if(binding.rvExList.getVisibility()==View.GONE)
                    showExSearchView();
            }
            else{
                hideExSearchView();
            }
        });
        binding.svPlace.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.onSearchSubmit(query);
                InputKeyboardUtil.hideKeyboard(getActivity());
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //presenter.onTextChange(newText);
                return true;
            }
        });
    }

    private void onSearchSubmit(String place){
        presenter.onSearchSubmit(place);
    }

    @Override
    public void showPositionInMap(LatLng latlon) {
        binding.svPlace.clearFocus();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlon,ZOOM));
        hideExSearchView();
    }

    public void showExSearchView() {
        binding.rvExList.setVisibility(View.VISIBLE);
        binding.viewBackground.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        presenter.initView();
    }

    @Override
    public void hideExSearchView(){
        binding.svPlace.clearFocus();
        binding.svPlace.setIconified(true);
        binding.rvExList.setVisibility(View.GONE);
        binding.viewBackground.setBackgroundColor(getResources().getColor(R.color.colorClear));
    }

    @Override
    public void showFragmentToast(String text) {
        binding.svPlace.clearFocus();
        super.showToast(text);
    }

    @Override
    public void showSearchResult() {

    }

    @Override
    public void showUserPin() {

    }

    @Override
    public void userPinClicked() {

    }

    @Override
    public void showPeriodSetting() {

    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mvGooglemap.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mvGooglemap.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mvGooglemap.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.mvGooglemap.onDestroy();
        if(disposable!=null)
            disposable.dispose();
    }

    @Override
    public void onDetach() {
        presenter.onDetach();
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.mvGooglemap.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.mvGooglemap.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.mvGooglemap.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap=googleMap;
        FusedLocationProviderClient fusedLocationClient= LocationServices.getFusedLocationProviderClient(getContext());
        if(ActivityCompat.checkSelfPermission(getContext(), TedPermissionUtil.LOCATION)== PackageManager.PERMISSION_GRANTED){
            fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                LatLng latlnt=new LatLng(task.getResult().getLatitude(),task.getResult().getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlnt,ZOOM));
            });
        }
        else{
            disposable = TedPermissionUtil.requestPermission(getContext(),getString(R.string.permission_location_title),getString(R.string.permission_location_message),TedPermissionUtil.LOCATION)
                    .subscribe(tedPermissionResult -> {
                        if(tedPermissionResult.isGranted()) {
                            fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                                LatLng latlnt = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlnt, ZOOM));
                            });
                        }
                    });
        }
    }
}
