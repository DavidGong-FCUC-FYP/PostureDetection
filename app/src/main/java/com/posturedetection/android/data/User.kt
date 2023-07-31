package com.posturedetection.android.data

class User {
    var name: String? = null
    var email: String? = null

    constructor() {}
    constructor(name: String?, email: String?) {
        this.name = name
        this.email = email
    }

    override fun toString(): String {
        return "User(name=$name, email=$email)"
    }


}