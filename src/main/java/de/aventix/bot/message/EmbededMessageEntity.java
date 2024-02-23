package de.aventix.bot.message;

import com.google.common.collect.Lists;
import lombok.*;

import java.util.List;

@Getter
@Setter
public class EmbededMessageEntity {
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Title {
        @Builder.Default
        private String title = null;
        @Builder.Default
        private String url = null;

        @Override
        public String toString() {
            return "Title{" +
                    "title='" + title + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Color {
        @Builder.Default
        private int r = 0;
        @Builder.Default
        private int g = 0;
        @Builder.Default
        private int b = 0;
        @Builder.Default
        private int a = 0;

        @Override
        public String toString() {
            return "Color{" +
                    "r=" + r +
                    ", g=" + g +
                    ", b=" + b +
                    ", a=" + a +
                    '}';
        }
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Field {
        @Builder.Default
        private String title = null;
        @Builder.Default
        private String description = null;
        @Builder.Default
        private boolean inline = false;

        @Override
        public String toString() {
            return "Field{" +
                    "title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", inline=" + inline +
                    '}';
        }
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Author {
        @Builder.Default
        private String name = null;
        @Builder.Default
        private String url = null;
        @Builder.Default
        private String iconUrl = null;

        @Override
        public String toString() {
            return "Author{" +
                    "name='" + name + '\'' +
                    ", url='" + url + '\'' +
                    ", iconUrl='" + iconUrl + '\'' +
                    '}';
        }
    }

    @Getter
    @Setter
    @Builder()
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Footer {
        @Builder.Default
        private String text = null;
        @Builder.Default
        private String iconUrl = null;

        @Override
        public String toString() {
            return "Footer{" +
                    "text='" + text + '\'' +
                    ", iconUrl='" + iconUrl + '\'' +
                    '}';
        }
    }

    private Title title = new Title();
    private Color color = new Color();
    private String description = null;
    private List<Field> fields = Lists.newArrayList();
    private List<Boolean> blankedFields = Lists.newArrayList();
    private Author author = new Author();
    private Footer footer = new Footer();
    private String image = null;
    private String thumbnail = null;

    public static EmbededMessageEntity getTestObject() {
        EmbededMessageEntity entity = new EmbededMessageEntity();

        entity.setTitle(Title.builder().title("Test-Title").url("https://google.de/").build());
        entity.setColor(Color.builder().r(235).g(58).b(52).build());
        entity.setDescription("Das ist eine Test Beschreibung");
        entity.getFields().add(Field.builder().title("Test Inline Field Title 1.").description("Das ist die erste Beschreibung").inline(true).build());
        entity.getFields().add(Field.builder().title("Test Field Title 2.").description("Das ist die zweite Beschreibung").inline(false).build());
        entity.getBlankedFields().add(false);
        entity.getBlankedFields().add(true);
        entity.setAuthor(Author.builder().name("Der Authorname").url("https://google.de/").iconUrl("ICON URL").build());
        entity.setFooter(Footer.builder().text("Das ist ein Footertext").build());
        entity.setImage("IMAGE URL");
        entity.setThumbnail("THUMBNAIL URL");

        return entity;
    }

    @Override
    public String toString() {
        return "EmbededMessageEntity{" +
                "title=" + title +
                ", color=" + color +
                ", description='" + description + '\'' +
                ", fields=" + fields +
                ", blankedFields=" + blankedFields +
                ", author=" + author +
                ", footer=" + footer +
                ", image='" + image + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}
