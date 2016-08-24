/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.demo.registration;

import com.vaadin.data.Result;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.data.validator.EmailValidator;

class EmailOrPhoneValidator extends AbstractValidator<String> {

    private final EmailValidator emailValidator;

    EmailOrPhoneValidator() {
        super("");
        emailValidator = new EmailValidator(
                "The string '{0}' is not a valid email address");
    }

    @Override
    public Result<String> apply(String value) {
        String val = value;
        // remove all spaces
        val = val.replace(" ", "");
        // if string starts from +0-9 ignoring spaces
        if (!startsWithCountryCode(val)) {
            return emailValidator.apply(value);
        }
        String digits = val.substring(1);
        // if string contains only + and digits (ignoring spaces)
        if (!hasOnlyDigits(digits)) {
            return Result.error(String.format(
                    "The string '%s' is not a valid phone number. "
                            + "Phone numbers should start with a plus sign followed by digits.",
                    value));
        }
        // now there should be at least 10 digits
        if (digits.length() >= 10) {
            return Result.ok(val);
        }
        return Result.error(String.format(
                "The string '%s' is not a valid phone number. "
                        + "Phone should start with plus sign and contain at least 10 digits",
                value));
    }

    private boolean startsWithCountryCode(String phone) {
        return phone.length() >= 2 && phone.charAt(0) == '+'
                && Character.isDigit(phone.charAt(1));
    }

    private boolean hasOnlyDigits(String phone) {
        return phone.chars().allMatch(Character::isDigit);
    }

}
