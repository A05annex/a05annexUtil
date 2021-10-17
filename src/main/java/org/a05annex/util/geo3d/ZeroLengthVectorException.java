package org.a05annex.util.geo3d;

/**
 * The exception typically thrown in normalization functions when the vector being
 * normalized is so close to zero length (relative to floating point precision) that the
 * divide by length operation generates meaningless results or a floating point divide
 * by zero exception.
 * <p>
 * The <code>ZeroLengthVectorException</code> is a primordial exception so there is
 * no constructor that takes a reason or another exception to which this one is linked.
 * @since 1.0
 */
public class ZeroLengthVectorException extends RuntimeException {
    /**
     * Creates a new instance of <code>ZeroLengthVectorException</code>.
     */
    public ZeroLengthVectorException() {
    }
}
