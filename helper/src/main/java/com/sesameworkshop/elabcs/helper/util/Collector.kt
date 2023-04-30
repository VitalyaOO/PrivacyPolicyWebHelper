package com.sesameworkshop.elabcs.helper.util

class Collector(
    private var count : Int,
    private val collectCallBack : () -> Unit
) {
    fun tryCollect(){
        count--
        if(count==0){
            collectCallBack()
        }
    }
}