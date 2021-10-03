package ir.treeroot.psyaziadmin.fragment;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.muddzdev.styleabletoast.StyleableToast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ir.treeroot.psyaziadmin.Database.DatabaseSqlite;
import ir.treeroot.psyaziadmin.Interface.RecyclerViewClickInterface;
import ir.treeroot.psyaziadmin.Ui.Edit.Fragment.EditPostFragment;
import ir.treeroot.psyaziadmin.Utils.APIClient;
import ir.treeroot.psyaziadmin.model.AddPost;
import ir.treeroot.psyaziadmin.Interface.Api;
import ir.treeroot.psyaziadmin.Utils.ConnectNetwork;
import ir.treeroot.psyaziadmin.Utils.Link;
import ir.treeroot.psyaziadmin.R;
import ir.treeroot.psyaziadmin.adapter.PostAdapter;
import ir.treeroot.psyaziadmin.model.Admin;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/*


create delete post in sqlite and fetch data where post is null in response


*/








public class PostFragment extends Fragment implements RecyclerViewClickInterface {

    ShimmerFrameLayout shimmerFrameLayout;
    RecyclerView recyclerView;
    SearchView search;
    RequestQueue requestQueue;
    List<AddPost> addPosts = new ArrayList<>();
    SwipeRefreshLayout refresh;
    PostAdapter adapter;
    LinearLayout search_bar;
    Api request;
    LottieAnimationView animationView;
    DatabaseSqlite db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_post, container, false);
        init(v);

        requestQueue = Volley.newRequestQueue(requireActivity());
        request = APIClient.getApiClient(Link.url).create(Api.class);
        db=new DatabaseSqlite(getActivity());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setFadingEdgeLength(80);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);


        refresh.setOnRefreshListener(() -> {
            GetData();
            new Handler().postDelayed(() -> refresh.setRefreshing(false), 1000);
            animationView.setVisibility(View.GONE);

        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @SuppressWarnings("CollectionAddedToSelf")
            @Override
            public boolean onQueryTextSubmit(String query) {
                findPost(query);
                adapter = new PostAdapter(getContext(), addPosts, PostFragment.this);
                recyclerView.setAdapter(adapter);
                addPosts.removeAll(addPosts);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        deleteLocalData();
        GetData();


        return v;

    }

    public void init(View v) {
        search = v.findViewById(R.id.searchview);
        recyclerView = v.findViewById(R.id.recyclerview);
        search_bar = v.findViewById(R.id.search_bar);
        shimmerFrameLayout = v.findViewById(R.id.shimmer_layout);
        refresh = v.findViewById(R.id.refresh);
        animationView=v.findViewById(R.id.animationView);
    }

    public void deleteLocalData(){

        request.UpdateData().enqueue(new Callback<List<AddPost>>() {
            @Override
            public void onResponse(@NotNull Call<List<AddPost>> call, @NotNull Response<List<AddPost>> response) {


                List<AddPost> list = response.body();

                for (int i = 0; i < list.size(); i++) {
                    AddPost data = list.get(i);
                    db.deleteCourse(data);
                }



            }

            @Override
            public void onFailure(@NotNull Call<List<AddPost>> call, @NotNull Throwable t) {

            }
        });

    }

    public void GetData() {
        shimmerFrameLayout.startShimmer();

        if (!ConnectNetwork.isOnline(requireActivity())) {
            new StyleableToast
                    .Builder(requireActivity())
                    .text("اینترنت خود را چک کنید")
                    .textColor(Color.WHITE)
                    .backgroundColor(Color.parseColor("#FFFA3C3C"))
                    .font(R.font.isans)
                    .show();
        }
        if(db.ExitsPostTable()){
            List<AddPost> addPosts = db.postList();
            recyclerView.setVisibility(View.VISIBLE);

            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new PostAdapter(getContext(), addPosts,  PostFragment.this);
            recyclerView.setAdapter(adapter);
            recyclerView.setFadingEdgeLength(80);
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            shimmerFrameLayout.stopShimmer();
        }

        request.GetData().enqueue(new Callback<List<AddPost>>() {
            @Override
            public void onResponse(@NotNull Call<List<AddPost>> call, @NotNull Response<List<AddPost>> response) {

                recyclerView.setVisibility(View.VISIBLE);
                List<AddPost> list = null;
                try {
                    list = response.body();
                } catch (NullPointerException ignored) {

                }

                adapter = new PostAdapter(getContext(), list,  PostFragment.this);
                recyclerView.setAdapter(adapter);

                shimmerFrameLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.stopShimmer();
                db.InsertPost(list);

            }

            @Override
            public void onFailure(@NotNull Call<List<AddPost>> call, @NotNull Throwable t) {

            }
        });


    }

    public void findPost(String url) {
        String LINK = Link.URL_GET_Search + url;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, LINK,
                null, response -> {

                try {
                    JSONArray jsonArray = response.getJSONArray("search");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String titles = jsonObject.getString("title");
                        String aliasname = jsonObject.getString("aliasname");
                        String img = jsonObject.getString("img");
                        String image = jsonObject.getString("image");
                        String text = jsonObject.getString("text");
                        String id = jsonObject.getString("id");
                        String postid = jsonObject.getString("postid");
                        String format = jsonObject.getString("format");


                        AddPost addPost = new AddPost();

                        addPost.setTitle(titles);
                        addPost.setAliasname(aliasname);
                        addPost.setImg(img);
                        addPost.setImage(image);
                        addPost.setText(text);
                        addPost.setId(id);
                        addPost.setPostid(postid);
                        addPost.setFormat(format);
                        addPosts.add(addPost);

                        animationView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        shimmerFrameLayout.setVisibility(View.VISIBLE);
                        shimmerFrameLayout.stopShimmer();
                        adapter = new PostAdapter(getContext(), addPosts, PostFragment.this);
                        recyclerView.setAdapter(adapter);

                    }
                    if (jsonArray.toString().equals("[]")) {
                        animationView.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


        }, error -> {
        });
        requestQueue.add(jsonObjectRequest);
    }


    @Override
    public void onItemClick(AddPost addPost) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity());

        @SuppressLint("InflateParams")
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_sheet_post, null);
        bottomSheetDialog.setContentView(contentView);

        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        LinearLayout edit_post = contentView.findViewById(R.id.edit_post);
        LinearLayout delete = contentView.findViewById(R.id.delete);
        bottomSheetDialog.show();
        if (delete != null) {
            delete.setOnClickListener(v -> {
                bottomSheetDialog.cancel();
                new AlertDialog.Builder(requireActivity())
                        .setTitle(R.string.delete_item)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {


                            ProgressDialog pd = new ProgressDialog(getActivity());
                            pd.show();
                            pd.setContentView(R.layout.progressbar);
                            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            Call<Admin> call = request.DeletePost(addPost.getPostid());
                            call.enqueue(new Callback<Admin>() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onResponse(@NotNull Call<Admin> call, @NotNull Response<Admin> response) {
                                    assert response.body() != null;
                                    if (response.body().getResponse().equals("200")){
                                        new StyleableToast
                                                .Builder(requireActivity())
                                                .text("با موفقیت حذف شد")
                                                .textColor(Color.WHITE)
                                                .backgroundColor(Color.parseColor("#FF018786"))
                                                .font(R.font.isans)
                                                .show();
                                        pd.dismiss();
                                    }

                                }

                                @Override
                                public void onFailure(@NotNull Call<Admin> call, @NotNull Throwable t) {

                                }
                            });




                        })

                        .setNegativeButton(R.string.no, null)
                        .setIcon(R.drawable.warning)
                        .show();

            });

        }

        edit_post.setOnClickListener(v -> {

            Fragment fragment = new EditPostFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title" ,addPost.getTitle());
            bundle.putString("text" ,addPost.getText());
            bundle.putString("postid" ,addPost.getPostid());
            fragment.setArguments(bundle);

            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            bottomSheetDialog.cancel();

        });

    }

}