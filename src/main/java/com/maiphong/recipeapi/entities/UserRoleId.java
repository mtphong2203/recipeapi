package com.maiphong.recipeapi.entities;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserRoleId {
    private UUID userId;
    private UUID roleId;
}
