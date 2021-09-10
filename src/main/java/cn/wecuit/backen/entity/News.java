package cn.wecuit.backen.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class News {

    private String name;

    private String source;

    private List<Map<String,String>> tags;

    private PatternType pattern;

    private String uriExp;

    private boolean sort = false;

    private int pullVer = 1;

    @Override
    public String toString() {
        return "News{" +
                "name='" + name + '\'' +
                ", source='" + source + '\'' +
                ", tags=" + tags +
                ", pattern='" + pattern + '\'' +
                ", uriExp='" + uriExp + '\'' +
                ", sort=" + sort +
                ", pullVer=" + pullVer +
                '}';
    }

    @Data
    public static
    class PatternType{
        private String rule;
        private PatternPos pos;

        @Data
        public static class PatternPos{
            private Integer title;
            private Integer link;
            private Integer date;
        }
    }
}
