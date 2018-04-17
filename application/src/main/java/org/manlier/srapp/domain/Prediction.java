package org.manlier.srapp.domain;

import java.util.Objects;

public class Prediction {

    private String userName;

    private String compName;

    private String followCompName;

    private Float prediction;

    public Prediction() {
    }

    public Prediction(String userName, String compName, String followCompName, Float prediction) {
        this.userName = userName;
        this.compName = compName;
        this.followCompName = followCompName;
        this.prediction = prediction;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public String getFollowCompName() {
        return followCompName;
    }

    public void setFollowCompName(String followCompName) {
        this.followCompName = followCompName;
    }

    public Float getPrediction() {
        return prediction;
    }

    public void setPrediction(Float prediction) {
        this.prediction = prediction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prediction that = (Prediction) o;
        return Objects.equals(userName, that.userName) &&
                Objects.equals(compName, that.compName) &&
                Objects.equals(followCompName, that.followCompName) &&
                Objects.equals(prediction, that.prediction);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userName, compName, followCompName, prediction);
    }

    @Override
    public String toString() {
        return "Prediction{" +
                "userName='" + userName + '\'' +
                ", compName='" + compName + '\'' +
                ", followCompName='" + followCompName + '\'' +
                ", prediction=" + prediction +
                '}';
    }
}
