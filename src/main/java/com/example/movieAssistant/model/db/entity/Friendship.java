package com.example.movieAssistant.model.db.entity;

import com.example.movieAssistant.model.enums.RequestConfirmationStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "friendships")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name="user1_id")
    UserInfo user1;

    @ManyToOne
    @JoinColumn(name="user2_id")
    UserInfo user2;

    @Column(name = "is_withdrawn")
    boolean withdrawn;

    @Column(name = "status")
    RequestConfirmationStatus status;

    @Column(name = "created_at")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime updatedAt;

    public Friendship(UserInfo user1Id, UserInfo user2Id, boolean isWithdrawn, RequestConfirmationStatus status, LocalDateTime createdAt) {
        this.user1 = user1Id;
        this.user2 = user2Id;
        this.withdrawn = isWithdrawn;
        this.status = status;
        this.createdAt = createdAt;
    }
}
