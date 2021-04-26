package com.hippo.utils.countrypicker;

import android.content.Context;
import androidx.annotation.NonNull;
import android.telephony.TelephonyManager;
import androidx.fragment.app.FragmentManager;
import com.hippo.R;

import java.util.*;

public class CurrencyPicker implements CountryPickerDialog.CountryPickerDialogInteractionListener {


    // region Countries
    private final Country[] CURRENCIES = {
            new Country(1, "$", "USD", "United States dollar"),
            new Country(2, "‎€", "EUR", "Euro"),
            new Country(3, "¥‎", "JPY", "Japanese yen"),
            new Country(4, "£", "GBP", "Pound sterling"),
            new Country(5, "$","AUD", "Australian dollar"),
            new Country(6, "C$", "CAD", "Canadian dollar"),
            new Country(7, "Fr.", "CHF", "Swiss franc"),
            new Country(8, "¥", "CNY", "Chinese yuan"),
            new Country(9, "kr", "SEK", "Swedish krona"),
            new Country(10, "Mex$", "MXN", "Mexican peso"),
            new Country(11, "NZ$", "NZD", "New Zealand dollar"),
            new Country(12, "S$", "SGD", "Singapore dollar"),
            new Country(13, "HK$", "HKD", "Hong Kong dollar"),
            new Country(14, "kr", "NOK", "Norwegian krone"),
            new Country(15, "₩", "KRW", "South Korean won"),
            new Country(16, "₹", "INR", "Indian rupee"),
            new Country(17, "₽", "RUB", "Russian ruble"),
            new Country(18, "R", "ZAR", "South African rand"),
            new Country(19, "KSh", "KES", "Kenyan Shilling"),
            new Country(20, "ZK", "ZMW", "Zambian Kwacha"),
            new Country(21, "AED", "AED", "Arab Emirates Dirham"),
            new Country(22, "E£", "EGP", "Egyptian Pound"),
            new Country(23, "S", "PEN", "Peruvian Sol"),
            new Country(24, "UGX", "UGX", "Ugandan Shilling"),
            new Country(25, "ع.د","IQD", "Iraqi Dinar"),
            new Country(26, "﷼", "QAR", "Qatari Riyal"),
            new Country(27, "$", "COP", "Colombian Peso"),
            new Country(28, "kr", "SEK", "Swedish Krona"),
            new Country(29, "₦", "NGN", "Nigerian Naira"),
            new Country(30, "RM", "MYR", "Malaysian Ringgit"),
            new Country(31, "re", "RES", "res"),
    };
    // endregion

    // region Variables
    public static final int SORT_BY_NONE = 0;
    public static final int SORT_BY_NAME = 1;
    public static final int SORT_BY_ISO = 2;
    public static final int SORT_BY_DIAL_CODE = 3;
    private static final String COUNTRY_TAG = "COUNTRY_PICKER";

    private Context context;
    private int sortBy = SORT_BY_NONE;
    private OnCountryPickerListener onCountryPickerListener;
    private boolean canSearch = true;

    private List<Country> countries;
    // endregion

    // region Constructors
    private CurrencyPicker() {
    }

    CurrencyPicker(Builder builder) {
        sortBy = builder.sortBy;
        if (builder.onCountryPickerListener != null) {
            onCountryPickerListener = builder.onCountryPickerListener;
        }
        context = builder.context;
        canSearch = builder.canSearch;
        countries = new ArrayList<>(Arrays.asList(CURRENCIES));
        sortCountries(countries);
    }
    // endregion

    // region Listeners
    @Override
    public void sortCountries(@NonNull List<Country> countries) {
        Collections.sort(countries, new Comparator<Country>() {
            @Override
            public int compare(Country country1, Country country2) {
                return country1.getName().trim().compareToIgnoreCase(country2.getName().trim());
            }
        });
    }

    @Override
    public List<Country> getAllCountries() {
        return countries;
    }

    @Override
    public boolean canSearch() {
        return canSearch;
    }

    // endregion

    // region Utility Methods
    public void showDialog(@NonNull FragmentManager supportFragmentManager) {
        if (countries == null || countries.isEmpty()) {
            throw new IllegalArgumentException(context.getString(R.string.hippo_error_no_countries_found));
        } else {
            CountryPickerDialog countryPickerDialog = CountryPickerDialog.newInstance();
            if (onCountryPickerListener != null) {
                countryPickerDialog.setCountryPickerListener(onCountryPickerListener);
            }
            countryPickerDialog.setDialogInteractionListener(this);
            countryPickerDialog.show(supportFragmentManager, COUNTRY_TAG);
        }
    }

    public void setCountries(@NonNull List<Country> countries) {
        this.countries.clear();
        this.countries.addAll(countries);
        sortCountries(this.countries);
    }

    public Country getCountryFromSIM() {
        TelephonyManager telephonyManager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null
                && telephonyManager.getSimState() != TelephonyManager.SIM_STATE_ABSENT) {
            return getCountryByISO(telephonyManager.getSimCountryIso());
        }
        return null;
    }

    public Country getCountryByLocale(@NonNull Locale locale) {
        String countryIsoCode = locale.getISO3Country().substring(0, 2).toLowerCase();
        return getCountryByISO(countryIsoCode);
    }

    public Country getCountryByName(@NonNull String countryName) {
        countryName = countryName.toUpperCase();
        Country country = new Country();
        country.setName(countryName);
        int i = Arrays.binarySearch(CURRENCIES, country, new NameComparator());
        if (i < 0) {
            return null;
        } else {
            return CURRENCIES[i];
        }
    }

    public Country getCountryByISO(@NonNull String countryIsoCode) {
        countryIsoCode = countryIsoCode.toUpperCase();
        Country country = new Country();
        country.setCode(countryIsoCode);
        int i = Arrays.binarySearch(CURRENCIES, country, new ISOCodeComparator());
        if (i < 0) {
            return null;
        } else {
            return CURRENCIES[i];
        }
    }


    // endregion

    // region Builder
    public static class Builder {
        private Context context;
        private int sortBy = SORT_BY_NONE;
        private boolean canSearch = true;
        private OnCountryPickerListener onCountryPickerListener;

        public Builder with(@NonNull Context context) {
            this.context = context;
            return this;
        }

        public Builder sortBy(@NonNull int sortBy) {
            this.sortBy = sortBy;
            return this;
        }

        public Builder listener(@NonNull OnCountryPickerListener onCountryPickerListener) {
            this.onCountryPickerListener = onCountryPickerListener;
            return this;
        }

        public Builder canSearch(@NonNull boolean canSearch) {
            this.canSearch = canSearch;
            return this;
        }

        public CurrencyPicker build() {
            return new CurrencyPicker(this);
        }
    }
    // endregion

    // region Comparators
    public static class ISOCodeComparator implements Comparator<Country> {
        @Override
        public int compare(Country country, Country nextCountry) {
            return country.getCode().compareTo(nextCountry.getCode());
        }
    }

    public static class NameComparator implements Comparator<Country> {
        @Override
        public int compare(Country country, Country nextCountry) {
            return country.getName().compareTo(nextCountry.getName());
        }
    }
    // endregion
}


