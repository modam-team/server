ALTER TABLE friend
    ADD CONSTRAINT uc_friend_requester_receiver UNIQUE (request_id, receiver_id);