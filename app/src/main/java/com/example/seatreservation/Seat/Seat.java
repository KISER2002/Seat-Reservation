package com.example.seatreservation.Seat;

public class Seat {
    String idx;
    String id;
    String name;
    String seat_count;
    String in_use;
    String empty_seat;
    String is_office;

    public Seat() {

    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeat_count() {
        return seat_count;
    }

    public void setSeat_count(String seat_count) {
        this.seat_count = seat_count;
    }

    public String getIn_use() {
        return in_use;
    }

    public void setIn_use(String in_use) {
        this.in_use = in_use;
    }

    public String getEmpty_seat() {
        return empty_seat;
    }

    public void setEmpty_seat(String empty_seat) {
        this.empty_seat = empty_seat;
    }

    public String getIs_office() {
        return is_office;
    }

    public void setIs_office(String is_office) {
        this.is_office = is_office;
    }
}
