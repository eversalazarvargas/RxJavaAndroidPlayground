package com.example.everardo.rxjavaplayground.presenter

interface MVPContract {

    interface View {
        fun setPresenter(presenter: Presenter)
        fun setResult(result: String)
    }

    interface Presenter {
        fun setRoot()

        fun setBack()

        fun resume()

        fun pause()

        fun create()

        fun destroy()
    }

}