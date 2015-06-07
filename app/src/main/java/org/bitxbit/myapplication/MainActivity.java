package org.bitxbit.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int HEADER_HEIGHT = 480;
    private static final int OFFSET = 100
            ;

    private RecyclerView rv;

    private SectionHeaderAnimationAdapter adapter;
    private Toolbar toolbar;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rv = (RecyclerView) findViewById(R.id.recycler_test);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(llm);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int totalDy;
            private boolean blurbHidden;
            private boolean titleSmall;
            private boolean titleHidden;
            boolean scrollingUp;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalDy += dy;
                scrollingUp = (dy > 0);
                System.out.println("totalDy " + totalDy);
                if (scrollingUp) {
                    if (!titleHidden && !blurbHidden && totalDy > HEADER_HEIGHT / 2) {
                        adapter.hideHeader();
                        adapter.hideBlurbTextView();
                        toolbarTitle.setText("SECTION");
                        titleHidden = blurbHidden = true;
                    }

                    if (!titleSmall && !titleHidden && totalDy > ((HEADER_HEIGHT / 3) - OFFSET)) {
                        adapter.makeHeaderSmall();
                        titleSmall = true;
                    }
                } else {
                    if (blurbHidden && titleHidden && totalDy < (HEADER_HEIGHT - (HEADER_HEIGHT / 3)) ) {
                        adapter.showBlurbTextView();
                        adapter.showHeader();
                        toolbarTitle.setText("");
                        adapter.makeHeaderSmall();
                        blurbHidden = titleHidden = false;
                    }

                    if (titleSmall && !titleHidden && totalDy < HEADER_HEIGHT / 2) {
                        adapter.makeHeaderRegular();
                        titleSmall = false;
                    }
                }
            }
        });
        adapter = new SectionHeaderAnimationAdapter();
        rv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    private static class SectionHeaderAnimationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int HEADER = 0;
        private static final int TEXT = 2;
        private TextView blurbTextView;
        private TextView headerTextView;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            switch (viewType) {
                case HEADER:
                    HeaderViewHolder hvh = new HeaderViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_header, viewGroup, false));
                    blurbTextView = hvh.tvBlurb;
                    headerTextView = hvh.tvHeader;
                    return hvh;

                case TEXT:
                    SimpleViewHolder svh = new SimpleViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_simple, viewGroup, false));
                    return svh;
            }
            //shouldn't get here
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            switch (getItemViewType(position)) {
                case HEADER:
                    HeaderViewHolder hvh = (HeaderViewHolder) viewHolder;
                    break;

                case TEXT:
                    SimpleViewHolder svh = (SimpleViewHolder) viewHolder;
                    svh.tv.setText("I am at position " + position);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return 20;
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? HEADER :
                    TEXT;
        }

        public void hideBlurbTextView() {
            if (blurbTextView != null) blurbTextView.setVisibility(View.INVISIBLE);
        }

        public void showBlurbTextView() {
            if(blurbTextView != null) blurbTextView.setVisibility(View.VISIBLE);
        }

        public void showHeader() {
            headerTextView.setVisibility(View.VISIBLE);
        }
        public void hideHeader() {
            headerTextView.setVisibility(View.INVISIBLE);
        }
        public void makeHeaderSmall() {
            headerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        }
        public void makeHeaderRegular() {
            headerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
        }
    }

    private static class SimpleViewHolder extends RecyclerView.ViewHolder {
        private TextView tv;
        public SimpleViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.txt_simple);
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvBlurb;
        private TextView tvHeader;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            tvBlurb = (TextView) itemView.findViewById(R.id.txt_header_line2);
            tvHeader = (TextView) itemView.findViewById(R.id.txt_header_line1);
        }
    }
}
