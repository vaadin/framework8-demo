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

import java.util.regex.Pattern;

import com.vaadin.data.Result;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.data.validator.EmailValidator;

class EmailOrPhoneValidator extends AbstractValidator<String> {

    private final EmailValidator emailValidator;
    private static final Pattern PHONE_REGEXP = Pattern
            .compile("^[ ]*\\+[ ]*[0-9][ \\d]*$");

    private static final Pattern PHONE_PREFIX = Pattern
            .compile("^[ ]*\\+[ ]*[0-9].*$");

    EmailOrPhoneValidator() {
        super("");
        emailValidator = new EmailValidator(
                "The string '{0}' is not valid email address");
    }

    @Override
    public Result<String> apply(String value) {
        // if string starts from +0-9 ignoring spaces
        if (PHONE_PREFIX.matcher(value).matches()) {
            // if string contains only + and digits (ignoring spaces)
            if (PHONE_REGEXP.matcher(value).matches()) {
                // remove all spaces
                String val = value.replace(" ", "");
                // remove leading +
                String digits = val.substring(1, val.length());
                // now there should be at least 10 digits
                if (digits.length() >= 10) {
                    return Result.ok(val);
                } else {
                    return Result.error(String.format(
                            "The string '%s' is not valid phone. "
                                    + "Phone should start from +0-9 and contain at least 10 digits",
                            value));
                }
            } else {
                return Result.error(String.format(
                        "The string '%s' is not valid phone. "
                                + "Phone should start from +0-9 and contain only digits",
                        value));
            }
        } else {
            return emailValidator.apply(value);
        }
    }

}
