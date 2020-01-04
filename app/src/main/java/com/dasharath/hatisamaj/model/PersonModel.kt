package com.dasharath.hatisamaj.model

import java.io.Serializable

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

data class PostModel(
    var profileImage: String,
    var image: String,
    var userName: String,
    var description: String
)

data class People(
    var people: ArrayList<PeopleRegisterUid>
) {
    constructor() : this(ArrayList())
}

data class PeopleRegisterUid(
    var person_uid: RegisterPeopleModel
) {
    constructor() : this(RegisterPeopleModel(ArrayList()))
}

data class RegisterPeopleModel(
    var register_people: ArrayList<PersonUniqueId>
){
    constructor() : this(ArrayList())
}

    data class PersonUniqueId(
    var uniqueId: PersonDetailModel
){
    constructor() : this(PersonDetailModel("","","","","","","","","","","","","","","","","","","","","",""))
}

data class PersonDetailModel(
    var Other: String,
    var address: String,
    var age: String,
    var business_detail: String,
    var business_name: String,
    var cl: String,
    var job_class: String,
    var gender: String,
    var company_name: String,
    var designation: String,
    var education: String,
    var email: String,
    var fName: String,
    var image_url: String,
    var job_type: String,
    var mobile_no: String,
    var name: String,
    var pr: String,
    var register_as: String,
    var doc_id: String,
    var sName: String,
    var status: String
): Serializable {
    constructor() : this("","","","","","","","","","","","","","","","","","","","","","")
}

data class UserData(
    var device_token: String,
    var email: String,
    var name: String,
    var password: String,
    var userType: String
){
    constructor() : this("","","","","")
}