package org.manlier.srapp.domain;

/**
 * 用完构件1又用构件2的总次数
 */
public class TotalFreq {

    private String compName;

    private String followCompName;

    private Long totalFreq;

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

    public Long getTotalFreq() {
        return totalFreq;
    }

    public void setTotalFreq(Long totalFreq) {
        this.totalFreq = totalFreq;
    }

    @Override
    public String toString() {
        return "TotalFreq{" +
                "compName='" + compName + '\'' +
                ", followCompName='" + followCompName + '\'' +
                ", totalFreq=" + totalFreq +
                '}';
    }
}
