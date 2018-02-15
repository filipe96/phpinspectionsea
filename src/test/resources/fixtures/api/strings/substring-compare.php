<?php

function cases_holder() {
    return [
        strtolower(substr('', 0, <error descr="The specified length doesn't match the string length.">-3</error>)) == '',
        strtolower(substr('', 0, <error descr="The specified length doesn't match the string length.">-3</error>)) != '',
        strtolower(substr('', 0, <error descr="The specified length doesn't match the string length.">3</error>)) === '',
        strtolower(substr('', 0, <error descr="The specified length doesn't match the string length.">3</error>)) !== '',

        substr('', 0, <error descr="The specified length doesn't match the string length.">-3</error>) == '',
        substr('', 0, <error descr="The specified length doesn't match the string length.">3</error>) === '',

        substr('', <error descr="The specified length doesn't match the string length.">-3</error>) == '',
        substr('', <error descr="The specified length doesn't match the string length.">3</error>) === '',
        substr('', <error descr="The specified length doesn't match the string length.">-3</error>) === 'a',
        substr('', <error descr="The specified length doesn't match the string length.">3</error>) === 'a',

        substr('...', 0, -3) == '...',
        substr('...', 0, 3) === '...',
    ];
}