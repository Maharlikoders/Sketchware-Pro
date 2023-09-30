package com.besome.sketch;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.LifecycleOwner;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.besome.sketch.design.DesignActivity;
import com.besome.sketch.editor.manage.library.ProjectComparator;
import com.besome.sketch.projects.MyProjectSettingActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.sketchware.remod.R;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import a.a.a.aB;
import a.a.a.DA;
import a.a.a.DB;
import a.a.a.lC;
import a.a.a.wB;
import mod.SketchwareUtil;
import mod.elfilibustero.sketch.beans.GitHubBean;
import mod.elfilibustero.sketch.lib.git.Clone;
import mod.elfilibustero.sketch.lib.git.PullRemoteUpdates;
import mod.elfilibustero.sketch.lib.ui.CreateItemView;
import mod.elfilibustero.sketch.lib.utils.GitHubUtil;
import mod.hasrat.dialog.SketchDialog;
import mod.hey.studios.project.ProjectTracker;
import mod.hey.studios.project.backup.BackupRestoreManager;
import mod.hey.studios.util.Helper;

public class ProjectsFragment extends DA {
    private Executor executor = Executors.newSingleThreadExecutor();

    private SwipeRefreshLayout swipeRefresh;
    private SearchView projectsSearchView;
    private final ArrayList<HashMap<String, Object>> projectsList = new ArrayList<>();
    private ProjectsAdapter projectsAdapter;
    private DB preference;
    private LottieAnimationView loading;
    private RecyclerView myProjects;

    private BottomSheetBehavior<View> bottomSheetBehavior;

    private LinearLayout bottomSheetContent;

    public final ActivityResultLauncher<Intent> openProjectSettings = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            var data = result.getData();
            assert data != null;
            refreshProjectsList();
            if (data.getBooleanExtra("is_new", false)) {
                toDesignActivity(data.getStringExtra("sc_id"));
            }
        }
    });

    private final OnBackPressedCallback closeBottomSheet = new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {
            if (isBottomSheetExpanded()) {
                hideBottomSheet();
            }
        }
    };

    private void initialize(View view) {
        preference = new DB(requireContext(), "project");
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        loading = view.findViewById(R.id.loading_3balls);
        NestedScrollView bottomSheet = view.findViewById(R.id.bottom_sheet);
        bottomSheetContent = view.findViewById(R.id.bottom_sheet_content);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        requireActivity().getOnBackPressedDispatcher().addCallback((LifecycleOwner)this, closeBottomSheet);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    closeBottomSheet.setEnabled(true);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    closeBottomSheet.setEnabled(false);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        swipeRefresh.setOnRefreshListener(() -> {
            // Check storage access
            if (!c()) {
                swipeRefresh.setRefreshing(false);
                // Ask for it
                ((MainActivity) requireActivity()).s();
            } else {
                refreshProjectsList();
            }
        });

        myProjects = view.findViewById(R.id.myprojects);
        myProjects.setHasFixedSize(true);

        projectsAdapter = new ProjectsAdapter(this, new ArrayList<>(projectsList));
        myProjects.setAdapter(projectsAdapter);
        
        executor.execute(() -> refreshProjectsList());

        view.findViewById(R.id.bottom_sheet_content_holder).setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                showBottomSheet();
            } else {
                hideBottomSheet();
            }
        });

        addItem(CreateItemView.PROJECT, v -> {
            hideBottomSheet();
            toProjectSettingsActivity();
        });
        addItem(CreateItemView.RESTORE, v -> {
            hideBottomSheet();
            restoreProject();
        });
        addItem(CreateItemView.CLONE, v -> {
            hideBottomSheet();
            showGitCloneDialog();
        });

        myProjects.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy) {
            super.onScrolled(rv, dx, dy);
                if (isBottomSheetExpanded()) {
                    hideBottomSheet();
                }
            }
        });
    }

    public boolean isBottomSheetExpanded() {
        return bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED;
    }

    public void refreshProjectsList() {
        // Don't load project list without having permissions
        if (!c()) return;

        new Thread(() -> {
            synchronized (projectsList) {
                projectsList.clear();
                projectsList.addAll(lC.a());
                Collections.sort(projectsList, new ProjectComparator(preference.d("sortBy")));
            }

            requireActivity().runOnUiThread(() -> {
                if (swipeRefresh.isRefreshing()) swipeRefresh.setRefreshing(false);
                if (loading != null) {
                    ((ViewGroup) loading.getParent()).removeView(loading);
                    myProjects.setVisibility(View.VISIBLE);
                    loading = null;
                }
                projectsAdapter.setAllProjects(new ArrayList<>(projectsList));
                if (projectsSearchView != null)
                    projectsAdapter.filterData(projectsSearchView.getQuery().toString());
            });
        }).start();
    }

    @Override
    public void b(int requestCode) {
    }

    public void toDesignActivity(String sc_id) {
        Intent intent = new Intent(requireContext(), DesignActivity.class);
        ProjectTracker.setScId(sc_id);
        intent.putExtra("sc_id", sc_id);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        GitHubUtil gitUtil = new GitHubUtil(sc_id);
        GitHubBean bean = gitUtil.getBean();
        if (!bean.username.isEmpty() && !bean.token.isEmpty() && bean.useYn.equals("Y")) {
                new PullRemoteUpdates(requireActivity(), sc_id).execute(success -> {
                    if (success) {
                        final String errorMessage;
                        try {
                            requireActivity().runOnUiThread(() -> SketchwareUtil.toast("Generating project sources, please wait"));
                            gitUtil.build();
                            requireActivity().startActivity(intent);
                            return;
                        } catch (FileNotFoundException e) {
                            errorMessage = e.getMessage();
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                        }
                        requireActivity().runOnUiThread(() -> SketchwareUtil.toastError("Generating failed: " + errorMessage));
                    } else {
                        requireActivity().startActivity(intent);
                    }
                });
            return;
        }
        requireActivity().startActivity(intent);
    }

    @Override
    public void c(int requestCode) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void d() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).s();
        }
    }

    @Override
    public void e() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).s();
        }
    }

    public int getProjectsCount() {
        synchronized (projectsList) {
            return projectsList.size();
        }
    }

    public void toProjectSettingsActivity() {
        Intent intent = new Intent(getActivity(), MyProjectSettingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        openProjectSettings.launch(intent);
    }

    public void restoreProject() {
        (new BackupRestoreManager(getActivity(), this)).restore();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.projects_fragment_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        projectsSearchView = (SearchView) menu.findItem(R.id.searchProjects).getActionView();
        projectsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String s) {
                if (isBottomSheetExpanded()) {
                    hideBottomSheet();
                }
                projectsAdapter.filterData(s);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sortProject) {
            hideBottomSheet();
            showProjectSortingDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View viewGroup = inflater.inflate(R.layout.myprojects, parent, false);
        setHasOptionsMenu(true);
        initialize(viewGroup);
        return viewGroup;
    }

    public void hideBottomSheet() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void showBottomSheet() {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void addItem(int type, View.OnClickListener listener) {
        CreateItemView item = new CreateItemView(requireActivity());
        item.setTag(type);
        item.setData(type);
        item.setOnClickListener(listener);
        bottomSheetContent.addView(item);
    }

    private void showProjectSortingDialog() {
        SketchDialog dialog = new SketchDialog(requireActivity());
        dialog.setTitle("Sort options");
        View root = wB.a(requireActivity(), R.layout.sort_project_dialog);
        RadioButton sortByName = root.findViewById(R.id.sortByName);
        RadioButton sortByID = root.findViewById(R.id.sortByID);
        RadioButton sortOrderAsc = root.findViewById(R.id.sortOrderAsc);
        RadioButton sortOrderDesc = root.findViewById(R.id.sortOrderDesc);

        int storedValue = preference.a("sortBy", ProjectComparator.DEFAULT);
        if ((storedValue & ProjectComparator.SORT_BY_NAME) == ProjectComparator.SORT_BY_NAME) {
            sortByName.setChecked(true);
        }
        if ((storedValue & ProjectComparator.SORT_BY_ID) == ProjectComparator.SORT_BY_ID) {
            sortByID.setChecked(true);
        }
        if ((storedValue & ProjectComparator.SORT_ORDER_ASCENDING) == ProjectComparator.SORT_ORDER_ASCENDING) {
            sortOrderAsc.setChecked(true);
        }
        if ((storedValue & ProjectComparator.SORT_ORDER_DESCENDING) == ProjectComparator.SORT_ORDER_DESCENDING) {
            sortOrderDesc.setChecked(true);
        }
        dialog.setView(root);
        dialog.setPositiveButton("Save", v -> {
            int sortValue = 0;
            if (sortByName.isChecked()) {
                sortValue |= ProjectComparator.SORT_BY_NAME;
            }
            if (sortByID.isChecked()) {
                sortValue |= ProjectComparator.SORT_BY_ID;
            }
            if (sortOrderAsc.isChecked()) {
                sortValue |= ProjectComparator.SORT_ORDER_ASCENDING;
            }
            if (sortOrderDesc.isChecked()) {
                sortValue |= ProjectComparator.SORT_ORDER_DESCENDING;
            }
            preference.a("sortBy", sortValue, true);
            dialog.dismiss();
            refreshProjectsList();
        });
        dialog.setNegativeButton("Cancel", Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    private void showGitCloneDialog() {
        Executor executor = Executors.newFixedThreadPool(4);
        Handler handler = new Handler(Looper.getMainLooper());
        
        try {
            Clone clone = new Clone(requireActivity(), lC.b());
            clone.execute((boolean success, String sc_id, GitHubBean bean) -> {
                if (success) {
                    GitHubUtil gitUtil = new GitHubUtil(sc_id);
                    gitUtil.setBean(bean);
                    executor.execute(() -> {
                        final String errorMessage;
                        try {
                            requireActivity().runOnUiThread(() -> SketchwareUtil.toast("Generating project sources, please wait"));
                            gitUtil.build();
                            handler.post(() -> {
                                refreshProjectsList();
                                toDesignActivity(sc_id);
                            });
                            return;
                        } catch (FileNotFoundException e) {
                            errorMessage = e.getMessage();
                        } catch (Exception e) {
                            errorMessage = e.getMessage();
                        }
                        requireActivity().runOnUiThread(() -> SketchwareUtil.toastError("Generating failed: " + errorMessage));
                    });
                }
            });
        } catch (Exception e) {
            SketchwareUtil.toastError("Cloning failed: " + e.getMessage());
        }
    }
}
