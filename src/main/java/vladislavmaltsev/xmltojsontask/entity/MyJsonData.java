package vladislavmaltsev.xmltojsontask.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyJsonData {
    @JsonProperty("Data")
    private DataParsed data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public final static  class DataParsed {
        @JsonProperty("Method")
        private Method method;
        @JsonProperty("Process")
        private Process process;
        @JsonProperty("Layer")
        private String layer;
        @JsonProperty("Creation")
        private Creation creation;
        @JsonProperty("Type")
        private String type;
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public final static class Method {
            @JsonProperty("Name")
            private String name;
            @JsonProperty("Type")
            private String type;
            @JsonProperty("Assembly")
            private String assembly;
        }
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public final static class Process {
            @JsonProperty("Name")
            private String name;
            @JsonProperty("Id")
            private String id;
            @JsonProperty("Start")
            private Start start;
            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            public final static class Start {
                @JsonProperty("Epoch")
                private String epoch;
                @JsonProperty("Date")
                private String date;
            }
        }
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public final static class Creation {
            @JsonProperty("Epoch")
            private String epoch;
            @JsonProperty("Date")
            private String date;
        }

    }
}
