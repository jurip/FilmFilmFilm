package ru.xmn.filmfilmfilm.screens.filmdetails

import android.app.SharedElementCallback
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.transition.*
import android.transition.TransitionSet.ORDERING_TOGETHER
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_film_details.*
import ru.xmn.filmfilmfilm.R
import ru.xmn.filmfilmfilm.common.loadUrl
import ru.xmn.filmfilmfilm.common.views.ElasticDragDismissCoordinatorLayout
import ru.xmn.filmfilmfilm.services.omdb.OmdbResponse
import ru.xmn.filmfilmfilm.services.tmdb.TmdbCredits
import ru.xmn.filmfilmfilm.services.tmdb.TmdbMovieInfo
import java.lang.Exception
import android.view.Gravity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import ru.xmn.filmfilmfilm.common.delay
import ru.xmn.filmfilmfilm.common.dur


class FilmDetailsActivity : LifecycleActivity() {

    companion object {
        val POSTER_KEY = "FilmDetailsActivity.Poster"
        val FILM_IMDB_ID_KEY = "FilmDetailsActivity.Film"
    }

    lateinit private var chromeFader: ElasticDragDismissCoordinatorLayout.ElasticDragDismissCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_details)
        postponeEnterTransition()

        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onSharedElementEnd(sharedElementNames: MutableList<String>?, sharedElements: MutableList<View>?, sharedElementSnapshots: MutableList<View>?) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots)
                val visible = description_container.visibility == View.VISIBLE

                if (visible) {
                    runExitAnimation()
                } else {
                    runEnterAnimation()
                }
            }
        })

        chromeFader = ElasticDragDismissCoordinatorLayout.SystemChromeFader(this)

        val posterUrl = intent.getStringExtra(POSTER_KEY)
        val filmImdbId = intent.getStringExtra(FILM_IMDB_ID_KEY)

        val factory = FilmDetailsViewModel.Factory(
                application, filmImdbId)

        val model = ViewModelProviders.of(this, factory).get(FilmDetailsViewModel::class.java)

        subscribeToModel(model)
        loadPosterThenStartTransition(posterUrl)
    }

    private fun runEnterAnimation() {
        val set = TransitionSet()
        set.addTransition(Fade().delay(200).dur(200))
//        set.addTransition(Slide(Gravity.BOTTOM).dur(400))
        set.ordering = ORDERING_TOGETHER
        TransitionManager.beginDelayedTransition(description_container, set)
        description_container.visibility = View.VISIBLE
    }

    private fun runExitAnimation() {
        description_container.visibility = View.INVISIBLE
    }

    override fun onResume() {
        super.onResume()
        draggable.addListener(chromeFader);
    }

    override fun onPause() {
        super.onPause()
        draggable.removeListener(chromeFader);
    }

    private fun subscribeToModel(model: FilmDetailsViewModel) {
        model.filminfo.observe(this, Observer { it?.let { bindUi(it) } })
    }

    private fun bindUi(it: Triple<TmdbMovieInfo, TmdbCredits, OmdbResponse>) {
        val (info, credits, ratings) = it
        filmName.text = info.title
        genres.text = info.genres.map { it.name }.joinToString()
        info_view.text = info.overview
        ratings_info.text = ratings.Ratings.associateBy({ it.Source }, { it.Value })
                .map { "${it.key}: ${it.value}" }.joinToString(separator = " | ")
        val url = "https://image.tmdb.org/t/p/w500${info.backdrop_path}"
        expandedImage.loadUrl(url)

        cast.layoutManager = LinearLayoutManager(this)
        crew.layoutManager = LinearLayoutManager(this)
        cast.adapter = PeoplesAdapter().also { it.items = credits.cast?.filter { it.name != null }?.map { PeoplesAdapter.PersonItem(it.name!!, it.character ?: "") }?.take(7) ?: emptyList() }
        crew.adapter = PeoplesAdapter().also { it.items = credits.crew?.filter { it.name != null }?.map { PeoplesAdapter.PersonItem(it.name!!, it.job ?: "") }?.take(7) ?: emptyList() }
        content.fullScroll(View.FOCUS_UP)
        content.scrollTo(0,0)
    }

    private fun loadPosterThenStartTransition(posterUrl: String?) {
        Glide.with(this)
                .load(posterUrl)
                .centerCrop()
                .dontAnimate()
                .listener(object : RequestListener<String, GlideDrawable> {
                    override fun onResourceReady(resource: GlideDrawable?, model: String?, target: Target<GlideDrawable>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                        startPostponedEnterTransition()
                        return false;
                    }

                    override fun onException(e: Exception?, model: String?, target: Target<GlideDrawable>?, isFirstResource: Boolean): Boolean {
                        startPostponedEnterTransition()
                        return false;
                    }
                })
                .into(poster)
    }
}

