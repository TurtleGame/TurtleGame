CREATE TABLE attack
(
    id             INT AUTO_INCREMENT NOT NULL,
    name           VARCHAR(50)        NOT NULL,
    power          DOUBLE             NOT NULL,
    attack_type_id INT                NULL,
    CONSTRAINT pk_attack PRIMARY KEY (id)
);

CREATE TABLE attack_type
(
    id   INT AUTO_INCREMENT NOT NULL,
    name VARCHAR(50)        NOT NULL,
    CONSTRAINT pk_attack_type PRIMARY KEY (id)
);

CREATE TABLE chat_entry
(
    id      INT AUTO_INCREMENT NOT NULL,
    message VARCHAR(255)       NOT NULL,
    sent_at datetime           NOT NULL,
    user_id INT                NULL,
    CONSTRAINT pk_chat_entry PRIMARY KEY (id)
);

CREATE TABLE equipment
(
    id            INT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(50)        NOT NULL,
    `description` VARCHAR(100)       NULL,
    slot          VARCHAR(255)       NULL,
    attack        INT                NULL,
    defence       INT                NULL,
    CONSTRAINT pk_equipment PRIMARY KEY (id)
);

CREATE TABLE expedition
(
    id            INT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(40)        NOT NULL,
    `description` VARCHAR(40)        NOT NULL,
    gold          DOUBLE             NOT NULL,
    min_level     INT                NOT NULL,
    CONSTRAINT pk_expedition PRIMARY KEY (id)
);

CREATE TABLE expedition_equipment
(
    id         INT AUTO_INCREMENT NOT NULL,
    chance     INT                NOT NULL,
    equipment  INT                NULL,
    expedition INT                NULL,
    CONSTRAINT pk_expedition_equipment PRIMARY KEY (id)
);

CREATE TABLE private_message
(
    id           INT AUTO_INCREMENT NOT NULL,
    subject      VARCHAR(250)       NOT NULL,
    content      VARCHAR(255)       NOT NULL,
    sent_at      datetime           NOT NULL,
    sender_id    INT                NULL,
    recipient_id INT                NULL,
    CONSTRAINT pk_private_message PRIMARY KEY (id)
);

CREATE TABLE report
(
    id        INT AUTO_INCREMENT NOT NULL,
    content   VARCHAR(255)       NOT NULL,
    sent_at   datetime           NOT NULL,
    title     VARCHAR(255)       NOT NULL,
    user_id   INT                NULL,
    turtle_id INT                NULL,
    CONSTRAINT pk_report PRIMARY KEY (id)
);

CREATE TABLE rarity
(
    id   INT AUTO_INCREMENT NOT NULL,
    name VARCHAR(50)        NOT NULL,
    CONSTRAINT pk_rarity PRIMARY KEY (id)
);

CREATE TABLE `role`
(
    id   INT AUTO_INCREMENT NOT NULL,
    name VARCHAR(20)        NOT NULL,
    CONSTRAINT pk_role PRIMARY KEY (id)
);

CREATE TABLE species_attack
(
    id             INT AUTO_INCREMENT NOT NULL,
    attack_id      INT                NULL,
    turtle_type_id INT                NULL,
    CONSTRAINT pk_species_attack PRIMARY KEY (id)
);

CREATE TABLE statistic
(
    id   INT AUTO_INCREMENT NOT NULL,
    name VARCHAR(50)        NOT NULL,
    CONSTRAINT pk_statistic PRIMARY KEY (id)
);

CREATE TABLE turtle
(
    id                INT AUTO_INCREMENT NOT NULL,
    name              VARCHAR(50)        NOT NULL,
    level             INT                NOT NULL,
    unassigned_points INT                NOT NULL,
    type_id           INT                NULL,
    is_available      INT                NULL,
    CONSTRAINT pk_turtle PRIMARY KEY (id)
);

CREATE TABLE turtle_battle_history
(
    id         INT AUTO_INCREMENT NOT NULL,
    winner_id INT                NULL,
    loser_id   INT                NULL,
    created_at datetime           NOT NULL,
    CONSTRAINT pk_turtle_battle_history PRIMARY KEY (id)
);

CREATE TABLE turtle_care
(
    id         INT AUTO_INCREMENT NOT NULL,
    created_at datetime           NOT NULL,
    turtle_id  INT                NULL,
    CONSTRAINT pk_turtle_care PRIMARY KEY (id)
);

CREATE TABLE turtle_expedition_history
(
    id            INT AUTO_INCREMENT NOT NULL,
    start_at      datetime           NOT NULL,
    end_at        datetime           NOT NULL,
    turtle_id     INT                NULL,
    expedition_id INT                NULL,
    was_rewarded  INT DEFAULT 0      NOT NULL,
    CONSTRAINT pk_turtle_expedition_history PRIMARY KEY (id)
);

CREATE TABLE turtle_owner_history
(
    id        INT AUTO_INCREMENT NOT NULL,
    start_at  datetime           NOT NULL,
    end_at    datetime           NULL,
    user_id   INT                NULL,
    turtle_id INT                NULL,
    CONSTRAINT pk_turtle_owner_history PRIMARY KEY (id)
);

CREATE TABLE turtle_statistic
(
    id           INT AUTO_INCREMENT NOT NULL,
    value        INT                NOT NULL,
    statistic_id INT                NULL,
    turtle_id    INT                NULL,
    CONSTRAINT pk_turtle_statistic PRIMARY KEY (id)
);

CREATE TABLE turtle_training_history
(
    id        INT AUTO_INCREMENT NOT NULL,
    start_at  datetime           NOT NULL,
    end_at    datetime           NOT NULL,
    skill     VARCHAR(255)       NULL,
    turtle_id INT                NULL,
    CONSTRAINT pk_turtle_training_history PRIMARY KEY (id)
);

CREATE TABLE turtle_transtation_history
(
    id            INT AUTO_INCREMENT NOT NULL,
    created_at    datetime           NOT NULL,
    turtle_id     INT                NULL,
    past_owner_id INT                NULL,
    new_owner_id  INT                NULL,
    CONSTRAINT pk_turtle_transtation_history PRIMARY KEY (id)
);

CREATE TABLE turtle_type
(
    id            INT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(50)        NOT NULL,
    `description` VARCHAR(250)       NOT NULL,
    rarity_id     INT                NULL,
    CONSTRAINT pk_turtle_type PRIMARY KEY (id)
);

CREATE TABLE user
(
    id                         INT AUTO_INCREMENT NOT NULL,
    activation_token           VARCHAR(300)       NULL,
    activation_token_expire_at datetime           NULL,
    username                   VARCHAR(15)        NOT NULL,
    password                   VARCHAR(64)        NOT NULL,
    email                      VARCHAR(50)        NOT NULL,
    is_email_confirmed         INT                NOT NULL,
    birthdate                  datetime           NULL,
    avatar_path                VARCHAR(300)       NULL,
    gold                       INT    NOT NULL,
    about                      VARCHAR(255)       NULL,
    role_id                    INT                NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE user_equipment_history
(
    id           INT AUTO_INCREMENT NOT NULL,
    start_at     datetime           NOT NULL,
    end_at       datetime           NULL,
    user_id      INT                NULL,
    equipment_id INT                NULL,
    turtle_id    INT                NULL,
    CONSTRAINT pk_user_equipment_history PRIMARY KEY (id)
);

CREATE TABLE user_status
(
    id                    INT AUTO_INCREMENT NOT NULL,
    start_at              datetime           NOT NULL,
    end_at                datetime           NULL,
    reason                VARCHAR(250)       NULL,
    user_status_action_id INT                NULL,
    giver_id              INT                NULL,
    user_id               INT                NULL,
    CONSTRAINT pk_user_status PRIMARY KEY (id)
);

CREATE TABLE user_status_action
(
    id   INT AUTO_INCREMENT NOT NULL,
    type VARCHAR(25)        NOT NULL,
    CONSTRAINT pk_user_status_action PRIMARY KEY (id)
);

ALTER TABLE attack
    ADD CONSTRAINT FK_ATTACK_ON_ATTACK_TYPE FOREIGN KEY (attack_type_id) REFERENCES attack_type (id);

ALTER TABLE chat_entry
    ADD CONSTRAINT FK_CHAT_ENTRY_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE expedition_equipment
    ADD CONSTRAINT FK_EXPEDITION_EQUIPMENT_ON_EQUIPMENT FOREIGN KEY (equipment) REFERENCES equipment (id);

ALTER TABLE expedition_equipment
    ADD CONSTRAINT FK_EXPEDITION_EQUIPMENT_ON_EXPEDITION FOREIGN KEY (expedition) REFERENCES expedition (id);

ALTER TABLE private_message
    ADD CONSTRAINT FK_PRIVATE_MESSAGE_ON_RECIPIENT FOREIGN KEY (recipient_id) REFERENCES user (id);

ALTER TABLE private_message
    ADD CONSTRAINT FK_PRIVATE_MESSAGE_ON_SENDER FOREIGN KEY (sender_id) REFERENCES user (id);

ALTER TABLE report
    ADD CONSTRAINT FK_RAPORT_ON_TURTLE FOREIGN KEY (turtle_id) REFERENCES turtle (id);

ALTER TABLE report
    ADD CONSTRAINT FK_RAPORT_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE species_attack
    ADD CONSTRAINT FK_SPECIES_ATTACK_ON_ATTACK FOREIGN KEY (attack_id) REFERENCES attack (id);

ALTER TABLE species_attack
    ADD CONSTRAINT FK_SPECIES_ATTACK_ON_TURTLE_TYPE FOREIGN KEY (turtle_type_id) REFERENCES turtle_type (id);

ALTER TABLE turtle_battle_history
    ADD CONSTRAINT FK_TURTLE_BATTLE_HISTORY_ON_LOSER FOREIGN KEY (loser_id) REFERENCES turtle (id);

ALTER TABLE turtle_battle_history
    ADD CONSTRAINT FK_TURTLE_BATTLE_HISTORY_ON_WINNDER FOREIGN KEY (winnder_id) REFERENCES turtle (id);

ALTER TABLE turtle_care
    ADD CONSTRAINT FK_TURTLE_CARE_ON_TURTLE FOREIGN KEY (turtle_id) REFERENCES turtle (id);

ALTER TABLE turtle_expedition_history
    ADD CONSTRAINT FK_TURTLE_EXPEDITION_HISTORY_ON_EXPEDITION FOREIGN KEY (expedition_id) REFERENCES expedition (id);

ALTER TABLE turtle_expedition_history
    ADD CONSTRAINT FK_TURTLE_EXPEDITION_HISTORY_ON_TURTLE FOREIGN KEY (turtle_id) REFERENCES turtle (id);

ALTER TABLE turtle
    ADD CONSTRAINT FK_TURTLE_ON_TYPE FOREIGN KEY (type_id) REFERENCES turtle_type (id);

ALTER TABLE turtle_owner_history
    ADD CONSTRAINT FK_TURTLE_OWNER_HISTORY_ON_TURTLE FOREIGN KEY (turtle_id) REFERENCES turtle (id);

ALTER TABLE turtle_owner_history
    ADD CONSTRAINT FK_TURTLE_OWNER_HISTORY_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE turtle_statistic
    ADD CONSTRAINT FK_TURTLE_STATISTIC_ON_STATISTIC FOREIGN KEY (statistic_id) REFERENCES statistic (id);

ALTER TABLE turtle_statistic
    ADD CONSTRAINT FK_TURTLE_STATISTIC_ON_TURTLE FOREIGN KEY (turtle_id) REFERENCES turtle (id);

ALTER TABLE turtle_training_history
    ADD CONSTRAINT FK_TURTLE_TRAINING_HISTORY_ON_TURTLE FOREIGN KEY (turtle_id) REFERENCES turtle (id);

ALTER TABLE turtle_transtation_history
    ADD CONSTRAINT FK_TURTLE_TRANSTATION_HISTORY_ON_NEW_OWNER FOREIGN KEY (new_owner_id) REFERENCES user (id);

ALTER TABLE turtle_transtation_history
    ADD CONSTRAINT FK_TURTLE_TRANSTATION_HISTORY_ON_PAST_OWNER FOREIGN KEY (past_owner_id) REFERENCES user (id);

ALTER TABLE turtle_transtation_history
    ADD CONSTRAINT FK_TURTLE_TRANSTATION_HISTORY_ON_TURTLE FOREIGN KEY (turtle_id) REFERENCES turtle (id);

ALTER TABLE turtle_type
    ADD CONSTRAINT FK_TURTLE_TYPE_ON_RARITY FOREIGN KEY (rarity_id) REFERENCES rarity (id);

ALTER TABLE user_equipment_history
    ADD CONSTRAINT FK_USER_EQUIPMENT_HISTORY_ON_EQUIPMENT FOREIGN KEY (equipment_id) REFERENCES equipment (id);

ALTER TABLE user_equipment_history
    ADD CONSTRAINT FK_USER_EQUIPMENT_HISTORY_ON_TURTLE FOREIGN KEY (turtle_id) REFERENCES turtle (id);

ALTER TABLE user_equipment_history
    ADD CONSTRAINT FK_USER_EQUIPMENT_HISTORY_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE user
    ADD CONSTRAINT FK_USER_ON_ROLE FOREIGN KEY (role_id) REFERENCES `role` (id);

ALTER TABLE user_status
    ADD CONSTRAINT FK_USER_STATUS_ON_GIVER FOREIGN KEY (giver_id) REFERENCES user (id);

ALTER TABLE user_status
    ADD CONSTRAINT FK_USER_STATUS_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE user_status
    ADD CONSTRAINT FK_USER_STATUS_ON_USER_STATUS_ACTION FOREIGN KEY (user_status_action_id) REFERENCES user_status_action (id);