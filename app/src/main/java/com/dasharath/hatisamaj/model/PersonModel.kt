package com.dasharath.hatisamaj.model

data class PersonModel(
    var image: String,
    var name: String,
    var age: String,
    var gender: String,
    var department: String
)

data class NotificationModel(
    var imageUser: String,
    var imagePerson: String,
    var name: String,
    var description: String,
    var gender: String
)