ALTER TABLE notification
DROP CHECK notification_chk_1;

ALTER TABLE notification
MODIFY COLUMN type VARCHAR(50) NOT NULL;

ALTER TABLE notification
ADD CONSTRAINT notification_type_chk
CHECK (
    type IN (
        'LOST_PET',
        'FOUND_PET',
        'IN_ADOPTION_PET',
        'IN_ADOPTION_AND_TRANSIT_PET',
        'NEW_VACCINATION_CAMPAIGN',
        'NEW_DONATION_CAMPAIGN',
        'NEW_CASTRATION_CAMPAIGN',
        'NEW_CARRIAGE_REQUEST'
    )
);