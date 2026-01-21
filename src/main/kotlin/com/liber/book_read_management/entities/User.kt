package com.liber.book_read_management.entities

import com.liber.book_read_management.enums.GenderType
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
    @Column(name = "phone_number")
    var phoneNumber: String = "",
    @Enumerated(EnumType.STRING)
    var gender: GenderType,
    var address: String? = null,
    @Column(name = "address_detail")
    var addressDetail: String? = null,
    @Column(name = "address_latitude")
    var addressLatitude: Double? = null,
    @Column(name = "address_longitude")
    var addressLongitude: Double? = null,
    @Column(name = "access_token")
    var accessToken: String? = null,
    @Column(name = "refresh_token")
    var refreshToken: String? = null
) : BaseTimeEntity()