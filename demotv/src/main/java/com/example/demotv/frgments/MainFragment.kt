package com.example.demotv.frgments

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.OnItemViewSelectedListener
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowPresenter
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.common.interfaces.DashBoardListener
import com.example.common.models.Articles
import com.example.common.models.Root
import com.example.common.viewmodels.DashboardViewModel
import com.example.demotv.Movie
import com.example.demotv.MovieList
import com.example.demotv.R
import com.example.demotv.activities.BrowseErrorActivity
import com.example.demotv.activities.DetailsActivity
import com.example.demotv.preseters.CardPresenter
import com.google.gson.Gson
import org.koin.android.ext.android.get
import java.util.*

/**
 * Loads a grid of cards with movies to browse.
 */
class MainFragment : BrowseSupportFragment(), DashBoardListener {

    private val mHandler = Handler()
    private lateinit var mBackgroundManager: BackgroundManager
    private var mDefaultBackground: Drawable? = null
    private lateinit var mMetrics: DisplayMetrics
    private var mBackgroundTimer: Timer? = null
    private var mBackgroundUri: String? = null
    private var list: List<Articles>? = null
    private var viewModel: DashboardViewModel? = null


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onActivityCreated(savedInstanceState)

        prepareBackgroundManager()

        setupUIElements()

        loadRows()

        setupEventListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: " + mBackgroundTimer?.toString())
        mBackgroundTimer?.cancel()
    }

    private fun prepareBackgroundManager() {

        mBackgroundManager = BackgroundManager.getInstance(activity)
        mBackgroundManager.attach(requireActivity().window)
        mDefaultBackground = ContextCompat.getDrawable(
            requireActivity(),
            R.drawable.default_background
        )
        mMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(mMetrics)
    }

    private fun setupUIElements() {
        title = getString(R.string.browse_title)
        // over title
        headersState = BrowseSupportFragment.HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        // set fastLane (or headers) background color
        brandColor = ContextCompat.getColor(requireActivity(), R.color.fastlane_background)
        // set search icon color
        searchAffordanceColor = ContextCompat.getColor(requireActivity(), R.color.search_opaque)
    }

    private fun loadRows() {
        val headers = resources.getStringArray(R.array.headers)
        viewModel = get<DashboardViewModel>()
        viewModel?.getNewsFeed()

        //temporary
        val i = activity?.resources?.openRawResource(R.raw.text)
        val scanner = Scanner(i)
        val builder = StringBuilder()
        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine())
        }
        val gson = Gson()
        list = gson.fromJson(builder.toString(), Root::class.java).articles

//        viewModel?.newsDataFeed?.observe(viewLifecycleOwner) { root ->
//            list = root.articles
            val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
            val cardPresenter = CardPresenter(this)

            for (j in 0 until NUM_ROWS) {
                if (j != 0) {
                    list?.shuffled()
                }
                val listRowAdapter = ArrayObjectAdapter(cardPresenter)

                viewModel?.getSavedNews()?.observe(viewLifecycleOwner) {
                    if (j == NUM_ROWS - 1) {
                        listRowAdapter.clear()
                        it.forEach { articles ->
                            listRowAdapter.add(articles)
                        }
                    } else {
                        val updatedList = mutableListOf<Articles>()
                        list?.forEach{articles->
                            it.forEach {savedArticles->
                                if(savedArticles.title == articles.title){
                                    articles.isBookmarked = true
                                    return@forEach
                                }
                            }
                            updatedList.add(articles)
                        }
                        for (k in 0 until NUM_COLS) {
                            listRowAdapter.add(updatedList[k])
                        }
                    }
                }
                val header = HeaderItem(j.toLong(), headers[j])
                rowsAdapter.add(ListRow(header, listRowAdapter))
            }

            adapter = rowsAdapter
//        }
    }

    private fun setupEventListeners() {
        setOnSearchClickedListener {
            Toast.makeText(requireActivity(), "Implement your own in-app search", Toast.LENGTH_LONG)
                .show()
        }

        onItemViewClickedListener = ItemViewClickedListener()
        onItemViewSelectedListener = ItemViewSelectedListener()
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder,
            item: Any,
            rowViewHolder: RowPresenter.ViewHolder,
            row: Row
        ) {

            if (item is Articles) {
                Log.d(TAG, "Item: $item")
                val intent = Intent(activity!!, DetailsActivity::class.java)
                intent.putExtra(DetailsActivity.NEWS, item)

                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity!!,
                    (itemViewHolder.view as ImageCardView).mainImageView,
                    DetailsActivity.SHARED_ELEMENT_NAME
                )
                    .toBundle()
                startActivity(intent, bundle)
            } else if (item is String) {
                if (item.contains(getString(R.string.error_fragment))) {
                    val intent = Intent(activity!!, BrowseErrorActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(activity!!, item, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(
            itemViewHolder: Presenter.ViewHolder?, item: Any?,
            rowViewHolder: RowPresenter.ViewHolder, row: Row
        ) {
            if (item is Articles) {
                mBackgroundUri = item.urlToImage
                startBackgroundTimer()
            }
        }
    }

    private fun updateBackground(uri: String?) {
        val width = mMetrics.widthPixels
        val height = mMetrics.heightPixels
        Glide.with(requireActivity())
            .load(uri)
            .centerCrop()
            .error(mDefaultBackground)
            .into<SimpleTarget<Drawable>>(
                object : SimpleTarget<Drawable>(width, height) {
                    override fun onResourceReady(
                        drawable: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        mBackgroundManager.drawable = drawable
                    }
                })
        mBackgroundTimer?.cancel()
    }

    private fun startBackgroundTimer() {
        mBackgroundTimer?.cancel()
        mBackgroundTimer = Timer()
        mBackgroundTimer?.schedule(UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY.toLong())
    }

    private inner class UpdateBackgroundTask : TimerTask() {

        override fun run() {
            mHandler.post { updateBackground(mBackgroundUri) }
        }
    }

    private inner class GridItemPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
            val view = TextView(parent.context)
            view.layoutParams = ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT)
            view.isFocusable = true
            view.isFocusableInTouchMode = true
            view.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.default_background))
            view.setTextColor(Color.WHITE)
            view.gravity = Gravity.CENTER
            return Presenter.ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
            (viewHolder.view as TextView).text = item as String
        }

        override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {}
    }

    companion object {
        private val TAG = "MainFragment"

        private val BACKGROUND_UPDATE_DELAY = 300
        private val GRID_ITEM_WIDTH = 200
        private val GRID_ITEM_HEIGHT = 200
        private val NUM_ROWS = 2
        private val NUM_COLS = 7
    }

    override fun onClickBookmark(article: Articles) {
        loadRows()
        if (article.isBookmarked)
            viewModel?.deleteArticle(article)
        else{
            article.isBookmarked = true
            viewModel?.saveArticle(article)
        }
    }

    override fun onClickArticle(article: Articles) {
        //for Mobile app
    }
}