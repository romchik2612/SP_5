/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sysprogrammingmainalgorithm;

/** 
 *
 * @author VVN
 */
import java.lang.*; 
import java.util.*;

public class Sysprogrammingmainalgorithm {

 public static void main(String[] args) {
     String readline;
     boolean result;
     String fileName;
     MyLang testLang = null;
     int codeAction, llk = 1, textLen;
     String[] menu = {"*1.  Прочитати граматику з файла  ",
             "2.  Надрукувати граматику",
             "3. Побудувати множини FirstK(A), A-нетермінал",
             "4. Вивести на термінал множини FirstK(A), A-нетермінал"
     };

     Scanner scan = new Scanner(System.in);
     do {
         codeAction = 0;
         String upr;
         for (String ss : menu) System.out.println(ss); // вивести меню
         System.out.println("Введіть код дії або end:");
         do {  // цикл перебору даних
             try {
                 readline = scan.nextLine();
                 upr = readline;
                 if (upr.trim().equals("end")) return;
                 codeAction = new Integer(upr.trim());
             } catch (Exception ee) {
                 System.out.println("Невірний код дії, повторіть: ");
                 continue;
             }
             if (codeAction >= 1 && codeAction <= menu.length) {
                 if (menu[codeAction - 1].substring(0, 1).equals("+")) {
                     System.out.println("Елемент меню " + codeAction + " повторно виконати неможливо");
                     continue;
                 }
                 int itmp;
                 for (itmp = 0; itmp < codeAction - 1; itmp++)
                     if (menu[itmp].substring(0, 1).equals("*")) break;
                 break;
             } else {
                 System.out.println("Невірний код дії, повторіть: ");
                 continue;
             }
         } while (true);
         // перевірка на виконання усіх попередніх дій
         result = false;
         switch (codeAction) {
             case 1: //1. Прочитати граматику з файла",
                 System.out.println("Введіть ім'я файлу граматики: ");
                 try {
                     readline = scan.nextLine();
                     fileName = readline;
                     System.out.println("Ім'я файлу граматики: " + fileName);
                     fileName = fileName.trim();
                 } catch (Exception ee) {
                     System.out.println("Системна помилка: " + ee.toString());
                     return;
                 }
                 System.out.println("Введіть значення параметра k : ");
                 try {
                     readline = scan.nextLine();
                     String llkText = readline;
                     llkText = llkText.trim();
                     llk = Integer.parseInt(llkText);
                 } catch (Exception ee) {
                     System.out.println("Системна помилка: " + ee.toString());
                     return;
                 }
                 testLang = new MyLang(fileName, llk);
                 if (!testLang.isCreate()) break;  //не створили об'єкт
                 System.out.println("Граматика прочитана успішно");
                 result = true;
                 for (int jj = 0; jj < menu.length; jj++) {
                     if (menu[jj].substring(0, 1).equals(" ")) continue;
                     menu[jj] = menu[jj].replace(menu[jj].charAt(0), '*');
                 }
                 break;
             case 2:  // Надрукувати граматику
                 testLang.printGramma();
                 break;
             case 3:  //Побудувати множини FirstK
                 LlkContext[] firstContext = testLang.firstK();
                 testLang.setFirstK(firstContext);
                 result = true;
                 break;
             case 4:  //Друк множини FirstK
                 testLang.printFirstkContext();
                 break;
         }  // кінець switch
         // блокуємо елемент обробки
         if (result) // функція виконана успішно
             if (menu[codeAction - 1].substring(0, 1).equals("*"))
                 menu[codeAction - 1] = menu[codeAction - 1].replace('*', '+');
     } while (true);  //глобальний цикл  обробки

 }  // кінець main
 
 }

