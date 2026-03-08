package com.liber.book_read_management.entities

import com.liber.book_read_management.enums.GenderType
import jakarta.persistence.*
import java.time.LocalTime

@Entity
@Table(name = "USER")
class User (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null,
    @Column(name = "email")
    var email: String = "",
    var password: String = "",
    @Column(name = "nickname")
    var nickname: String = "",
    @Column(name = "photo_id")
    var photoId: Long? = null,
//    var address: String? = null,
//    @Column(name = "address_detail")
//    var addressDetail: String? = null,
//    @Column(name = "address_latitude")
//    var addressLatitude: Double? = null,
//    @Column(name = "address_longitude")
//    var addressLongitude: Double? = null,
    @Column(name = "access_token")
    var accessToken: String? = null,
    @Column(name = "refresh_token")
    var refreshToken: String? = null,
    @Column(name = "monthly_goal_pages")
    var monthlyGoalPages: Int? = null,
    @Column(name = "yearly_goal_pages")
    var yearlyGoalPages: Int? = null,
    @Column(name = "notification_enabled")
    var notificationEnabled: Boolean = false,
    @Column(name = "notification_time")
    var notificationTime: LocalTime? = null,
    @Column(name = "fcm_token", length = 1000)
    var fcmToken: String? = null
) : BaseTimeEntity()
