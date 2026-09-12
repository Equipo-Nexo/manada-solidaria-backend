package com.nexo.manada_solidaria_backend.password_recovery.data.enums;

import java.util.EnumSet;
import java.util.Set;

public enum PasswordRecoveryStatus {
    ACTIVE,
    VERIFIED,
    USED,
    REVOKED;

    public static final Set<PasswordRecoveryStatus> OPEN = EnumSet.of(ACTIVE, VERIFIED);
}
