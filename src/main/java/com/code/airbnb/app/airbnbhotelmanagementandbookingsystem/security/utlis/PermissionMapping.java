package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.utlis;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.entities.Permissions;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.entities.Role;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.Set;

import static com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.entities.Permissions.*;
import static com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.entities.Role.*;

@UtilityClass
public class PermissionMapping {

    private static final Map<Role, Set<Permissions>> ROLE_PERMISSION_MAP = Map.of(
            USER, Set.of(
                    HOTEL_VIEW,
                    ROOM_VIEW,
                    BOOKING_CREATE,
                    BOOKING_VIEW,
                    BOOKING_UPDATE,
                    BOOKING_CANCEL
            ),

            HOTEL_MANAGER, Set.of(
                    HOTEL_VIEW,
                    ROOM_VIEW,
                    ROOM_CREATE,
                    ROOM_UPDATE,
                    ROOM_DELETE,
                    BOOKING_VIEW,
                    BOOKING_UPDATE,
                    BOOKING_CANCEL,
                    INVENTORY_VIEW,
                    INVENTORY_UPDATE
            ),

            HOTEL_ADMIN, Set.of(
                    HOTEL_CREATE,
                    HOTEL_VIEW,
                    HOTEL_UPDATE,
                    HOTEL_DELETE,
                    ROOM_CREATE,
                    ROOM_VIEW,
                    ROOM_UPDATE,
                    ROOM_DELETE,
                    BOOKING_VIEW,
                    BOOKING_UPDATE,
                    BOOKING_CANCEL,
                    INVENTORY_CREATE,
                    INVENTORY_VIEW,
                    INVENTORY_UPDATE,
                    INVENTORY_DELETE
            ),

            ROOMLY_ADMIN, Set.of(Permissions.values())
    );

    public static Set<Permissions> getPermissionsForRole(Role role) {
        return ROLE_PERMISSION_MAP.getOrDefault(role, Set.of());
    }

}
