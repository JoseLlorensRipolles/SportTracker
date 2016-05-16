/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testgpx2;

/**
 *
 * @author JoseManuel
 */
public class DependencyInjector {
    public static Presenter getPresenter(ActivityView view){
        return new Presenter(view);
    }

}
