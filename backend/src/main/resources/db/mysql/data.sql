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


