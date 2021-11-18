package com.csye6225.springapi.springmvcrest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.UUID;

    @Entity
    @Table(name = "image_data")
    public class Profile {
        @Id
        private String id;
        private String file_name;
        private String url;
        private String upload_date;
        private String userid;
        public Profile() {

        }

        public Profile(String file_name, String url, String upload_date, String userid) {
            this.id =  UUID.randomUUID().toString();
            this.file_name = file_name;
            this.url = url;
            this.upload_date = upload_date;
            this.userid = userid;
        }


        public String getId() {
            return id;
        }

        public String getFile_name() {
            return file_name;
        }

        public void setFile_name(String file_name) {
            this.file_name = file_name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUpload_date() {
            return upload_date;
        }

        public void setUpload_date(String upload_date) {
            this.upload_date = upload_date;
        }

        public String getUser_id() {
            return userid;
        }

        public void setUser_id(String userid) {
            this.userid = userid;
        }
    }

