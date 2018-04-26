package org.manlier.srapp.constraints;

/**
 * 同步信号
 */
public enum SynSignal {

    DICT_SYN_REQ,   //  请求同步字典
    DICT_SYN_DONE,  //  字典同步完成
    SYN_DONE        //  重建索引完成，同步完成
}
