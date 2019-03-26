package com.lin;

/**
 * @author jianglinzou
 * @date 2019/3/21 上午10:51
 */
public interface ShouldAccept {


    /**
     * 如果返回true,将会触发对应的方法，返回false,相应的方法将不会被触发
     *
     * @return
     */
    boolean accept();


}
