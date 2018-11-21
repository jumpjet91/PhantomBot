/*
 * Copyright (C) 2016-2018 phantombot.tv
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package tv.phantombot.scripts.core;

import java.util.HashMap;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 * This system has a lot of repeating code, mostly because when matching spam, 
 * once we hit the limit, we no longer need to keep matching, so we return true.
 * This doesn't allow us to make functions to get "totals" of things and reuse them.
 * 
 * @author ScaniaTV
// */
public final class Moderation {
    private static final Moderation INSTANCE = new Moderation();
    private final Pattern URL_PATTERN = Pattern.compile("((?:(http|https|rtsp):\\\\/\\\\/(?:(?:[a-z0-9\\\\$\\\\-\\\\_\\\\.\\\\+\\\\!\\\\*\\\\\\'\\\\(\\\\)\\\\,\\\\;\\\\?\\\\&\\\\=]|(?:\\\\%[a-fA-F0-9]{2})){1,64}(?:\\\\:(?:[a-z0-9\\\\$\\\\-\\\\_\\\\.\\\\+\\\\!\\\\*\\\\\\'\\\\(\\\\)\\\\,\\\\;\\\\?\\\\&\\\\=]|(?:\\\\%[a-fA-F0-9]{2})){1,25})?\\\\@)?)?((?:(?:[a-z0-9][a-z0-9\\\\-]{0,64}\\\\.)+(?:(?:aero|a[cdefgilmnoqrstuwxz])|(?:biz|b[abdefghijmnorstvwyz])|(?:com|c[acdfghiklmnoruvxyz])|d[ejkmoz]|(?:edu|e[cegrstu])|(?:fyi|f[ijkmor])|(?:gov|g[abdefghilmnpqrstuwy])|(?:how|h[kmnrtu])|(?:info|i[delmnoqrst])|(?:jobs|j[emop])|k[eghimnrwyz]|l[abcikrstuvy]|(?:mil|mobi|moe|m[acdeghklmnopqrstuvwxyz])|(?:name|net|n[acefgilopruz])|(?:org|om)|(?:pro|p[aefghklmnrstwy])|qa|(?:r[eouw])|(?:s[abcdeghijklmnortuvyz])|(?:t[cdfghjklmnoprtvwz])|u[agkmsyz]|(?:vote|v[ceginu])|(?:xxx)|(?:watch|w[fs])|y[etu]|z[amw]))|(?:(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])))(?:\\\\:\\\\d{1,5})?)(\\\\/(?:(?:[a-z0-9\\\\;\\\\/\\\\?\\\\:\\\\@\\\\&\\\\=\\\\#\\\\~\\\\-\\\\.\\\\+\\\\!\\\\*\\\\\\'\\\\(\\\\)\\\\,\\\\_])|(?:\\\\%[a-fA-F0-9]{2}))*)?(?:\\\\b|$)|(\\\\.[a-z]+\\\\/|magnet:\\/\\/|mailto:\\/\\/|ed2k:\\/\\/|irc:\\/\\/|ircs:\\/\\/|skype:\\/\\/|ymsgr:\\/\\/|xfire:\\/\\/|steam:\\/\\/|aim:\\/\\/|spotify:\\/\\/)");
    private final Pattern YOUTUBE_PATTERN = Pattern.compile("!\\w{1,9}\\s((http(s)?:\\/\\/)?youtu(\\.be)?(be\\.com))");
    /**
     * Class constructor.
     */
    private Moderation() {
        
    }
    
    /**
     * Method that returns this instance.
     * 
     * @return 
     */
    public static Moderation instance() {
        return INSTANCE;
    }
    
    /**
     * Method that checks if the message has a URL.
     * 
     * @param message
     * @param isSongrequestsEnabled
     * @return 
     */
    public boolean hasURL(String message, boolean isSongrequestsEnabled) {
        boolean hasMatch = URL_PATTERN.matcher(message).matches();
        
        if (isSongrequestsEnabled && YOUTUBE_PATTERN.matcher(message).matches()) {
            hasMatch = false;
        }
        
        return hasMatch;
    }
    
    /**
     * Method that checks if a message or username has a blacklist.
     * 
     * @param username
     * @param message
     * @param blacklist
     * @return 
     */
    public boolean hasBlacklist(String username, String message, JSONObject blacklist) {
        boolean hasBlacklist = false;
        
        if (blacklist.has("_total") && blacklist.getInt("_total") > 0) {
            JSONArray list = blacklist.getJSONArray("list");
            
            for (int i = 0; i < list.length(); i++) {
                JSONArray words = list.getJSONArray(i);
                for (int j = 0; j < words.length(); j++) {
                    JSONObject wordObj = words.getJSONObject(i);
                    
                    // Do things.
                }
            }
        }
        return hasBlacklist;
    }
    
    /**
     * Method that says if a string has too many non alpha chars.
     * 
     * @param message
     * @param maxNonAlpha
     * @param maxPercent
     * @return 
     */
    public boolean hasMaximumNonAlphanumeric(String message, int maxNonAlpha, float maxPercent) {
        boolean hasMaximumNonAlpha = false;
        int messageLength = message.length();
        int totalNonAlphanumericCount = 0;
        
        for (int i = 0; i < messageLength; i++) {
            char c = message.charAt(i);
            
            if (!(Character.isLetterOrDigit(c) || Character.isWhitespace(c))) {
                totalNonAlphanumericCount++;
                if (totalNonAlphanumericCount >= maxNonAlpha || ((totalNonAlphanumericCount / messageLength) >= maxPercent)) {
                    hasMaximumNonAlpha = true;
                    break;
                }
            }
        }
        
        return hasMaximumNonAlpha;
    }
    
    /**
     * Method that checks if multiple non-alphanumeric characters (random ones) are repeating multiple times in a row.
     * 
     * @param message
     * @param maxLength
     * @return 
     */
    public boolean hasLongNonAlphanumericSequence(String message, int maxLength) {
        boolean hasLongRepeatingSequence = false;
        int totalRepeatingSequence = 0;
        
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            
            if (!(Character.isLetterOrDigit(c) || Character.isWhitespace(c))) {
                totalRepeatingSequence++;
                if (totalRepeatingSequence >= maxLength) {
                    hasLongRepeatingSequence = true;
                    break;
                }
            } else {
                totalRepeatingSequence = 0;
            }
        }
        
        return hasLongRepeatingSequence;
    }
    
    /**
     * Method that checks if a character is repeated multiple times in a row.
     * 
     * @param message
     * @param maxLength
     * @return 
     */
    public boolean hasLongCharacterSequence(String message, int maxLength) {
        boolean hasLongRepeatingSequence = false;
        int totalRepeatingSequence = 0;
        char lastCharacter = message.charAt(0);
        
        for (int i = 1; i < message.length(); i++) {
            char c = message.charAt(i);
            
            if (c == lastCharacter) {
                totalRepeatingSequence++;
                if (totalRepeatingSequence >= maxLength) {
                    hasLongRepeatingSequence = true;
                    break;
                }
            } else {
                totalRepeatingSequence = 0;
            }
            lastCharacter = c;
        }
        
        return hasLongRepeatingSequence;
    }
    
    /**
     * Method that checks if a word is repeated multiple times in a for.
     * 
     * @param message
     * @param maxLength
     * @param rawEmoteIndexes
     * @return 
     */
    public boolean hasLongWordSequence(String message, int maxLength, String rawEmoteIndexes) {
        boolean hasLongRepeatingSequence = false;     
        int totalRepeatingSequence = 0;
        String[] messageParts = getMessageWithoutEmotes(message, rawEmoteIndexes).split(" ");
        String lastWord = messageParts[0];

        for (int i = 1; i < messageParts.length; i++) {
            String word = messageParts[i];
            
            if (word.equalsIgnoreCase(lastWord)) {
                totalRepeatingSequence++;
                if (totalRepeatingSequence >= maxLength) {
                    hasLongRepeatingSequence = true;
                    break;
                }
            } else {
                totalRepeatingSequence = 0;
            }
            lastWord = word;
        }
        
        return hasLongRepeatingSequence;
    }
    
    /**
     * Method that gets a map of the emote indexes.
     * 
     * @param rawEmoteIndexes
     * @return 
     */
    private HashMap<Integer, Integer> getEmotesIndexMap(String rawEmoteIndexes) {
        HashMap<Integer, Integer> emoteIndexMap = new HashMap<>();
        String[] indexArray = rawEmoteIndexes.split("/");
        
        for (int i = 0; i < indexArray.length; i++) {
            String[] indexes = indexArray[i].substring(indexArray[i].indexOf(":") + 1).split(",");
            for (int j = 0; j < indexes.length; j++) {
                String[] index = indexes[j].split("-");
                
                if (index.length > 1) {
                    emoteIndexMap.put(Integer.parseInt(index[0]), Integer.parseInt(index[1]));
                }
            }
        }
        
        return emoteIndexMap;
    }
    
    /**
     * Method that removes all emotes from a message.
     * 
     * @param message
     * @param rawEmoteIndexes
     * @return 
     */
    private String getMessageWithoutEmotes(String message, String rawEmoteIndexes) {
        HashMap<Integer, Integer> emoteIndexMap = getEmotesIndexMap(rawEmoteIndexes);
        
        if (!emoteIndexMap.isEmpty()) {
            for (int i = message.length() - 1; i >= 0; i--) {
                if (emoteIndexMap.containsKey(i)) {
                    message = message.substring(0, i) + message.substring(emoteIndexMap.get(i) + 1);
                }
            }
        }

        return message.trim();
    }
    
    /**
     * Method that says if a message has too many caps.
     * 
     * @param message
     * @param maxCaps
     * @param maxPercent
     * @param rawEmoteIndexes
     * @return 
     */
    public boolean hasMaximumCaps(String message, int maxCaps, float maxPercent, String rawEmoteIndexes) {
        // Remove Twitch emotes from the message.
        message = getMessageWithoutEmotes(message, rawEmoteIndexes);
        
        boolean hasMaxCaps = false;
        int messageLength = message.length();
        int totalCapsCount = 0;

        for (int i = 0; i < messageLength; i++) {
            char c = message.charAt(i);
            
            if (Character.isUpperCase(c)) {
                totalCapsCount++;
                if (totalCapsCount >= maxCaps || ((totalCapsCount / messageLength) >= maxPercent)) {
                    hasMaxCaps = true;
                    break;
                }
            }
        }
        
        return hasMaxCaps;
    }
    
    /**
     * Method that checks if a message is in color.
     * 
     * @param message
     * @return 
     */
    public boolean hasColorMessage(String message) {
        return (message.startsWith("/me ")); // The space is required for the color.
    }
    
    /**
     * Method that checks if a message is a fake purge.
     * 
     * @param message
     * @return 
     */
    public boolean hasFakePurge(String message) {
        boolean hasFakePurge = false;
        
        // Removed the colored settings, if any.
        if (message.startsWith("/me ")) {
            message = message.substring(4); // Remove 3 + 1 for the space.
        }
        
        // Check these.
        if (message.startsWith("<message") && message.endsWith(">")) {
            hasFakePurge = true;
        } else if (message.startsWith("<") && message.endsWith("deleted>")) {
            hasFakePurge = true;
        }
        
        return hasFakePurge;
    }
}
