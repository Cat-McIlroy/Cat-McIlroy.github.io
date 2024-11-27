/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package hotel.reservation.system;

/**
 *
 * @author catherinemcilroy
 */
public class Application {
    public static void main(String[] args) {

        // instantiate new objects
        UserInterface ui = new UserInterface("Test");

        String roomType = ui.getRequiredRoomType();

        Room room = new Room(roomType);

        ui.welcomeMenu(room);
    }
}


