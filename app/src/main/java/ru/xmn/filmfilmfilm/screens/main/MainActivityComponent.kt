package ru.xmn.filmfilmfilm.screens.main

import dagger.Subcomponent
import ru.xmn.filmfilmfilm.screens.main.films.di.FilmsComponent
import ru.xmn.filmfilmfilm.screens.main.films.di.FilmsModule

@Subcomponent(modules = arrayOf(
        MainActivityModule::class
))
interface MainActivityComponent {
    fun inject(mainActivity: MainActivity)
    fun plus(filmsModule: FilmsModule): FilmsComponent
}