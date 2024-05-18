    package com.hxjt.dqyt.bean;

    import java.util.Map;

    public class MenuButtonBean {
        public int resourceId;
        public String name;
        public String typeCode;

        public int getResourceId() {
            return resourceId;
        }

        public void setResourceId(int resourceId) {
            this.resourceId = resourceId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTypeCode() {
            return typeCode;
        }

        public void setTypeCode(String typeCode) {
            this.typeCode = typeCode;
        }

        // 从 Map 中创建 MenuButtonBean 对象
        public static MenuButtonBean fromMap(Map<String, Object> map) {
            MenuButtonBean menuButton = new MenuButtonBean();
            if (map != null) {
                // 从 Map 中获取对应的值，并设置到 MenuButtonBean 对象中
                if (map.containsKey("name")) {
                    menuButton.setName((String) map.get("name"));
                }
                if (map.containsKey("resourceId")) {
                    menuButton.setResourceId((int) map.get("resourceId"));
                }

                if (map.containsKey("typeCode")) {
                    menuButton.setTypeCode((String) map.get("typeCode"));
                }
            }
            return menuButton;
        }
    }
