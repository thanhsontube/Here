package son.nt.here.dto;

import java.util.List;

import son.nt.here.MsConst;

/**
 * Created by Sonnt on 4/30/15.
 */
public class FavouriteDto {
    private final double lat;
    private final double lgn;
    private String address;
    private String title;
    private String notes;
    private List<String> images;
    private MsConst.PlaceType placeType;

    public FavouriteDto(double lat, double lgn, String title, String address, String notes, List<String> images, MsConst.PlaceType placeType) {
        this.placeType = placeType;
        this.lat = lat;
        this.lgn = lgn;
        this.title = title;
        this.address = address;
        this.notes = notes;
        this.images = images;
    }

    public FavouriteDto(double lat, double lgn) {
        this.lat = lat;
        this.lgn = lgn;
    }

    public static class Builder {
        private double lat;
        private double lgn;
        private String title;
        private String address;
        private String notes;
        private List<String> images;
        private MsConst.PlaceType placeType;

        public Builder position(double lat, double lgn) {
            this.lat = lat;
            this.lgn = lgn;
            return this;
        }

        public Builder title (String title) {
            this.title = title;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder image(String path) {
            images.add(path);
            return this;
        }

        public Builder placeType(MsConst.PlaceType placeType) {
            this.placeType = placeType;
            return this;
        }

        public FavouriteDto build() {
            return new FavouriteDto(lat, lgn, title, address, notes, images, placeType);
        }
    }

    public double getLat() {
        return lat;
    }

    public double getLgn() {
        return lgn;
    }

    public String getAddress() {
        return address;
    }

    public String getNotes() {
        return notes;
    }

    public List<String> getImages() {
        return images;
    }

    public MsConst.PlaceType getPlaceType() {
        return placeType;
    }

    public String getTitle() {
        return title;
    }
}
