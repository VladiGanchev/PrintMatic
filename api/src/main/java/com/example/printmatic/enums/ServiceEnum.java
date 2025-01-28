package com.example.printmatic.enums;

public enum ServiceEnum {
    A3 {
        @Override
        public String bulgarianName() {
            return "А3";
        }
    },
    A4 {
        @Override
        public String bulgarianName() {
            return "А4";
        }
    },
    A5 {
        @Override
        public String bulgarianName() {
            return "А5";
        }
    },
    GRAYSCALE {
        @Override
        public String bulgarianName() {
            return "черно-бели";
        }
    },
    COLOR {
        @Override
        public String bulgarianName() {
            return "цветни";
        }
    },
    REGULAR_MATE {
        @Override
        public String bulgarianName() {
            return "стандартна";
        }
    },
    GLOSSY {
        @Override
        public String bulgarianName() {
            return "гланцирана";
        }
    },
    BRIGHT_WHITE {
        @Override
        public String bulgarianName() {
            return "ярко бяла";
        }
    },
    PHOTO {
        @Override
        public String bulgarianName() {
            return "фотохартия";
        }
    },
    HEAVYWEIGHT {
        @Override
        public String bulgarianName() {
            return "тежка (плътна)";
        }
    },
    ONE_HOUR {
        @Override
        public String bulgarianName() {
            return "1 час";
        }
    },
    ONE_DAY {
        @Override
        public String bulgarianName() {
            return "1 ден";
        }
    },
    THREE_DAYS {
        @Override
        public String bulgarianName() {
            return "3 дена";
        }
    },
    ONE_WEEK {
        @Override
        public String bulgarianName() {
            return "1 седмица";
        }
    };

    public abstract String bulgarianName();
}
