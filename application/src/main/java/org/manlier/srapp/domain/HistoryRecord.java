package org.manlier.srapp.domain;

/**
 * 历史纪录
 */
public class HistoryRecord {

    private String userName;

    private String compName;

    private String followCompName;

    private Long freq;

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

    public Long getFreq() {
        return freq;
    }

    public void setFreq(Long freq) {
        this.freq = freq;
    }

    @Override
    public String toString() {
        return "HistoryRecord{" +
                "userName='" + userName + '\'' +
                ", compName='" + compName + '\'' +
                ", followCompName='" + followCompName + '\'' +
                ", freq=" + freq +
                '}';
    }
}
