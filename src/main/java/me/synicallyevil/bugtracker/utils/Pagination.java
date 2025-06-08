/*
 *
 *     This file is part of BugTracker.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package me.synicallyevil.bugtracker.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Pagination {

    private HashMap<Integer, List<String>> pages = new HashMap<>();

    public Pagination(ArrayList<String> list){
        int j = 0;

        for(int i = 0; i < list.size(); i++){
            if((i % 10) == 0)
                j++;

            addToPage(j, list.get(i));
        }
    }

    private void addToPage(int page, String s){
        List<String> currentPage = new ArrayList<>();

        if(pages.containsKey(page)){
            currentPage = pages.get(page);
            currentPage.add(s);

            pages.replace(page, currentPage);
            return;
        }

        currentPage.add(s);
        pages.put(page, currentPage);
    }

    public int getTotalPages(){
        return pages.size();
    }

    public List<String> getListFromPage(int page){
        ArrayList<String> bugs = new ArrayList<>();

        if(isPage(page))
            bugs.addAll(pages.get(page));
        else
            bugs.addAll(pages.get(getLastPage()));

        return bugs;
    }

    private boolean isPage(int page){
        return pages.containsKey(page);
    }

    private int getLastPage(){
        if(pages.size() > 0)
            return pages.size();

        return 0;
    }
}