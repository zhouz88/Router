package com.zz.route_runtime

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log

object Router {
    private const val TAG = "RouterTAG"
    private const val GENERATED_MAPPING = "com.zz.kerwingo.RouterMapping"
    private val mapping: HashMap<String, String> = HashMap()


    fun init() {
        try {
            val clazz = Class.forName(GENERATED_MAPPING)
            val method = clazz.getMethod("get")
            val allMapping = method.invoke(null) as Map<String, String>?

            allMapping?.let {
                it.onEach{ entry ->
                    Log.d(TAG, "${entry.key} -> ${entry.value}")
                    mapping.put(entry.key, entry.value)
                }
            }
        } catch (e : Throwable) {
            Log.e(TAG, "init: error while init")
        }
    }

    fun go(context: Context?, url: String?) {
        if (context == null || url == null) {
            return
        }

        val uri = Uri.parse(url)
        val schema = uri.scheme
        val host = uri.host
        val authority = uri.authority
        val path = uri.path

        Log.d(TAG, " target >>>> ${schema}${host}${authority}${path}")
        var targetActivityClass = ""
        mapping.onEach {
            val ruri = Uri.parse(it.key)
            val rschema = ruri.scheme
            val rhost = ruri.host
            val rauthority = ruri.authority
            val rpath = ruri.path
            Log.d(TAG, " we found >>>> ${rschema}${rhost}${rauthority}${rpath}")
            if (rschema == schema && rhost == host && rpath == path) {
                targetActivityClass = it.value
            }
        }

        if (targetActivityClass.isNullOrEmpty()) {
            Log.e(TAG, "go:     no destination found.")
            return
        }

        val bundle = Bundle()
        val query = uri.query
        query?.let {
            if (it.length  >= 3) {
                val args = it.split("&")
                args.onEach { arg ->
                    val splits = arg.split("=")
                    bundle.putString(splits[0], splits[1])
                }
            }
        }
        try {
            val activity = Class.forName(targetActivityClass)
            val intent = Intent(context, activity)
            intent.putExtras(bundle)
            context.startActivity(intent)
        } catch (e: Exception) {
            val elements = e.stackTrace
            val builder = StringBuilder()
            for (i in 0 until  elements.size) {
                if (i < 14) {
                    val element = elements[i]
                    builder.append(element.className).append(".").append(element.methodName)
                        .append("\n")
                }
            }
            Log.e(TAG, "go: error while staring act: $targetActivityClass :xxx" + builder.toString())
        }
        //匹配URL 找到目标页面
        //解析参数 封装成一个bundle
        //打开对应的activity,传入参数
    }
}