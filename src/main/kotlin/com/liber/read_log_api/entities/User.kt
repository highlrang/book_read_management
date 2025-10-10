package com.liber.read_log_api.entities

import jakarta.persistence.*

@Entity
@Table(name = "USER")
class User (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null,
    @Column(name = "login_id")
    var loginId: String = "",
    var password: String = "",
    var name: String = "",
    var address: String? = null,
    var addressDetail: String? = null,
    var addressLatitude: Double? = null,
    var addressLongitude: Double? = null,
    var accessToken: String? = null,

) : BaseTimeEntity()