--비밀번호 : 1234
INSERT INTO MEMBERS (member_id, email, password, nickname, role, CREATED_BY, created_date, last_modified_by, last_modified_date)
VALUES (100, 'holeman@naver.com', '$2a$10$uUMqKLg/sYaIOTGmm/M8NOwXkGcsg0ijSslFRYNlUo8o8NIeye21W',
        'holeman', 'MEMBER', 'holeman@naver.com', CURRENT_TIMESTAMP(), 'holeman@naver.com', CURRENT_TIMESTAMP());
