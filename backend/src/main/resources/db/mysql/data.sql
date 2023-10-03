INSERT INTO category(name, image_url)
VALUES ('인기매물', 'https://i.ibb.co/LSkHKbL/star.png'),
       ('부동산', 'https://i.ibb.co/41ScRXr/real-estate.png'),
       ('중고차', 'https://i.ibb.co/bLW8sd7/car.png'),
       ('디지털기기', 'https://i.ibb.co/cxS7Fhc/digital.png'),
       ('생활가전', 'https://i.ibb.co/F5z7vV9/domestic.png'),
       ('가구/인테리어', 'https://i.ibb.co/cyYH5V8/furniture.png'),
       ('유아동', 'https://i.ibb.co/VNKYZTK/baby.png'),
       ('유아도서', 'https://i.ibb.co/LrwjRdf/baby-book.png'),
       ('스포츠/레저', 'https://i.ibb.co/hXVgTyd/sports.png'),
       ('여성잡화', 'https://i.ibb.co/yPwkyg3/woman-accessories.png'),
       ('여성의류', 'https://i.ibb.co/4fvj6SC/woman-apparel.png'),
       ('남성패션/잡화', 'https://i.ibb.co/wwfyjyB/man-apparel.png'),
       ('게임/취미', 'https://i.ibb.co/cwJ74M4/game.png'),
       ('뷰티/미용', 'https://i.ibb.co/cXrrK0m/beauty.png'),
       ('반려동물용품', 'https://i.ibb.co/CbwHdNr/pet.png'),
       ('도서/음반', 'https://i.ibb.co/7WjKkdt/book.png'),
       ('티켓,교환권', 'https://i.ibb.co/kBhhs2p/ticket.png'),
       ('생활', 'https://i.ibb.co/T0mnp8m/kitchen.png'),
       ('가공식품', 'https://i.ibb.co/S0rSyxr/processed-foods.png'),
       ('식물', 'https://i.ibb.co/rwZhRqh/plant.png'),
       ('기타 중고물품', 'https://i.ibb.co/tCyMPf5/etc.png'),
       ('삽니다', 'https://i.ibb.co/g7Gc1w0/buy.png');

LOAD DATA LOCAL INFILE 'src/main/resources/regions.csv'
    INTO TABLE region
    FIELDS TERMINATED BY ','
    IGNORE 1 ROWS
    (@col1) set name = @col1;

INSERT INTO member(avatar_url, email, login_id)
VALUES ('https://nid.naver.com/user2/api/route?m=routePcProfileModification',
        'dragonbead95@naver.com',
        'bruni');
INSERT INTO member(avatar_url, email, login_id)
VALUES ('https://nid.naver.com/user2/api/route?m=routePcProfileModification',
        'carlynne@naver.com',
        'carlynne');
INSERT INTO member(avatar_url, email, login_id)
VALUES ('https://nid.naver.com/user2/api/route?m=routePcProfileModification',
        'qkdlfjtm119@naver.com',
        'lee1234');

INSERT INTO member_town(name, member_id, region_id, is_selected)
VALUES ('역삼1동', 1, 294, true);
INSERT INTO member_town(name, member_id, region_id, is_selected)
VALUES ('역삼1동', 2, 294, true);
INSERT INTO member_town(name, member_id, region_id, is_selected)
VALUES ('역삼1동', 3, 294, true);

INSERT INTO item(chat_count,
                 content,
                 created_at,
                 price,
                 region,
                 status,
                 thumbnail_url,
                 title,
                 view_count,
                 wish_count,
                 category_id,
                 member_id)
VALUES (0, '롤러블레이드 팝니다', now(), 169000, '역삼1동', 'ON_SALE',
        'https://second-hand-team03-a.s3.ap-northeast-2.amazonaws.com/public/sample/roller_blade.jpeg',
        '롤러 블레이드', 0, 0, 1, 1);

INSERT INTO image (image_url, thumbnail, item_id)
VALUES ('https://second-hand-team03-a.s3.ap-northeast-2.amazonaws.com/public/sample/roller_blade.jpeg',
        true,
        1);

INSERT INTO item(chat_count,
                 content,
                 created_at,
                 price,
                 region,
                 status,
                 thumbnail_url,
                 title,
                 view_count,
                 wish_count,
                 category_id,
                 member_id)
VALUES (0, '롤러블레이드 팝니다2', now(), 169000, '역삼1동', 'ON_SALE',
        'https://second-hand-team03-a.s3.ap-northeast-2.amazonaws.com/public/sample/roller_blade.jpeg',
        '롤러 블레이드', 0, 0, 1, 1);

INSERT INTO image (image_url, thumbnail, item_id)
VALUES ('https://second-hand-team03-a.s3.ap-northeast-2.amazonaws.com/public/sample/roller_blade.jpeg',
        true,
        2);


INSERT INTO item(chat_count,
                 content,
                 created_at,
                 price,
                 region,
                 status,
                 thumbnail_url,
                 title,
                 view_count,
                 wish_count,
                 category_id,
                 member_id)
VALUES (0, '의자 팝니다.', now(), 130000, '역삼1동', 'ON_SALE',
        'https://second-hand-team03-a.s3.ap-northeast-2.amazonaws.com/public/sample/char.jpeg',
        '옛날 의자', 0, 0, 6, 1);

INSERT INTO image (image_url, thumbnail, item_id)
VALUES ('https://second-hand-team03-a.s3.ap-northeast-2.amazonaws.com/public/sample/char.jpeg',
        true,
        3);

INSERT INTO wish(created_at, item_id, member_id)
VALUES (now(), 1, 2);
INSERT INTO wish(created_at, item_id, member_id)
VALUES (now(), 2, 2);
INSERT INTO wish(created_at, item_id, member_id)
VALUES (now(), 3, 2);

INSERT INTO chat_room(created_at, item_id, member_id)
VALUES (now(), 1, 2);
INSERT INTO chat_room(created_at, item_id, member_id)
VALUES (now(), 1, 3);

INSERT INTO chat_log(created_at, message, receiver, sender, read_count, chat_room_id)
VALUES (now(), '안녕하세요. 롤러블레이브를 사고 싶습니다. 만원만 깍아주세요.', 'bruni', 'carlynne', 0, 1),
       (now(), '좋아요. 깍아서 15만원에 드릴게요.', 'carlynne', 'bruni', 0, 1),
       (now(), '택배비 포함인가요?', 'bruni', 'carlynne', 0, 1),
       (now(), '아쉽게도 택배비 포함안되서 16만원이에요.', 'carlynne', 'bruni', 0, 1),
       (now(), '알았어요. 16만원 보낼게요.', 'bruni', 'carlynne', 0, 1),
       (now(), '고마워요1', 'bruni', 'carlynne', 1, 1),
       (now(), '고마워요2', 'bruni', 'carlynne', 1, 1),
       (now(), '고마워요3', 'bruni', 'carlynne', 1, 1),
       (now(), '고마워요4', 'bruni', 'carlynne', 1, 1),
       (now(), '고마워요5', 'bruni', 'carlynne', 1, 1),
       (now(), '고마워요6', 'bruni', 'carlynne', 1, 1),
       (now(), '고마워요7', 'bruni', 'carlynne', 1, 1),
       (now(), '고마워요8', 'bruni', 'carlynne', 1, 1),
       (now(), '고마워요9', 'bruni', 'carlynne', 1, 1),
       (now(), '고마워요10', 'bruni', 'carlynne', 1, 1);

INSERT INTO chat_log(created_at, message, receiver, sender, read_count, chat_room_id)
VALUES (now(), '안녕하세요. 롤러블레이브를 사고 싶습니다. 만원만 깍아주세요.', 'bruni', 'lee1234', 0, 2),
       (now(), '좋아요. 깍아서 15만원에 드릴게요.', 'lee1234', 'bruni', 0, 2),
       (now(), '택배비 포함인가요?', 'bruni', 'lee1234', 0, 2),
       (now(), '아쉽게도 택배비 포함안되서 16만원이에요.', 'lee1234', 'bruni', 0, 2),
       (now(), '알았어요. 16만원 보낼게요.', 'bruni', 'lee1234', 0, 2),
       (now(), '고마워요1', 'bruni', 'lee1234', 1, 2),
       (now(), '고마워요2', 'bruni', 'lee1234', 1, 2),
       (now(), '고마워요3', 'bruni', 'lee1234', 1, 2),
       (now(), '고마워요4', 'bruni', 'lee1234', 1, 2),
       (now(), '고마워요5', 'bruni', 'lee1234', 1, 2),
       (now(), '고마워요6', 'bruni', 'lee1234', 1, 2),
       (now(), '고마워요7', 'bruni', 'lee1234', 1, 2),
       (now(), '고마워요8', 'bruni', 'lee1234', 1, 2),
       (now(), '고마워요9', 'bruni', 'lee1234', 1, 2),
       (now(), '고마워요10', 'bruni', 'lee1234', 1, 2);






