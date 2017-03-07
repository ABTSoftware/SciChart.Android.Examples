package com.scichart.scishowcase.model

import io.reactivex.Observable

interface IDataProvider<E> {
    fun getData() : Observable<E>
}