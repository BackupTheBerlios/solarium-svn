package solarium.swing;

import java.awt.Component;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.SwingUtilities;

/**
 * A thread whose status is displayed by a {@link JProgressBar progress bar}
 * dialog. The thread is responsible for updating its status using:
 * <ul>
 * <li>{@link #setMinimum(int)}</li>
 * <li>{@link #setProgress(int)}</li>
 * <li>{@link #setMaximum(int)}</li>
 * </ul>
 *
 * @author Ravi Narain Bhatia
 * @version 1.0
 * @see javax.swing.ProgressMonitor
 */

public class ProgressBarThread extends Thread {
    
    private JProgressBar progressBar;
    private JDialog dialog;
    private boolean cancelled;
    private JLabel messageLabel;
    private Component parentComponent;
    
    /**
     * Title used for the dialog.
     */
    public static final String DIALOG_TITLE = "Progress";
    
    /*
     * Constructor
     */
    public ProgressBarThread() {
        
        this(null);
    }
    
   /*
    * Constructor
    * @param Runnable
    */
    public ProgressBarThread(Runnable target) {
        
        super(target);
        
        // Initialize progress bar and message label.
        progressBar = new JProgressBar();
        messageLabel = new JLabel("Processing...", JLabel.LEFT);
    }
    
    /**
     * Set the parent component of the progress dialog.
     *
     * @param parentComponent The parent component.
     */
    public void setParentComponent(Component parentComponent) {
        
        this.parentComponent = JOptionPane.getFrameForComponent(parentComponent);
    }
    
    /**
     * Get the progress dialog.
     *
     * @return The progress dialog.
     */
    protected JDialog getDialog() {
        
        if (dialog != null) {
            return dialog;
        }
        
        // Cancel Button
        JButton cancelButton;
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                cancel();
            }
        });
        
        //Option Pane.
        Object[] objects = new Object[] { messageLabel, progressBar };
        JOptionPane optionPane =
                new JOptionPane(objects, JOptionPane.INFORMATION_MESSAGE);
        optionPane.setOptions(new Object[] { cancelButton });
        
        //Dialog
        dialog = optionPane.createDialog(parentComponent, DIALOG_TITLE);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            
            public void windowClosed(WindowEvent e) {
                
                cancel();
            }
        });
        
        return dialog;
    }
    
    private void cancel() {
        
        setCancelled(true);
        interrupt();
    }
    
    /**
     * Set if the progress bar is indeterminate. A progress is said to be 
     * indeterminate when its progress status cannot be 
     *
     * @param indeterminate
     *            <code>true</code>, if the progress bar is indeterminate.
     */
    public void setIndeterminate(final boolean indeterminate) {
        
        SwingUtilities.invokeLater(new Runnable() {
            
            public void run() {
                
                progressBar.setIndeterminate(indeterminate);
            }
        });
        
    }
    
    /**
     * Set the cancelled property to false.
     *
     * @param cancelled
     *            <code>true</code>, to signify that a task is to be
     *            cancelled.
     */
    protected void setCancelled(boolean cancelled) {
        
        this.cancelled = cancelled;
    }
    
    /**
     * Get the cancelled property.
     *
     * @return <code>true</code>, to signify that a task is to be cancelled.
     */
    public boolean getCancelled() {
        
        return cancelled;
    }
    
    /**
     * Set the progress bar maximum.
     *
     * @param maximum
     *            Progress bar maximum.
     */
    public void setMaximum(final int maximum) {
        
        SwingUtilities.invokeLater(new Runnable() {
            
            public void run() {
                
                progressBar.setMaximum(maximum);
            }
        });
    }
    
    /**
     * Get the progress bar maximum.
     *
     * @return Progress bar maximum.
     */
    public int getMaximium() {
        
        return progressBar.getMaximum();
    }
    
    /**
     * Set the progress bar minimum.
     *
     * @param minimum Progress bar minimum.
     */
    public void setMinimum(final int minimum) {
        
        SwingUtilities.invokeLater(new Runnable() {
            
            public void run() {
                
                progressBar.setMaximum(minimum);
            }
        });
    }
    
    /**
     * Get the progress bar minimum.
     *
     * @return Progress bar minimum.
     */
    public int getMinimum() {
        
        return progressBar.getMinimum();
    }
    
    /**
     * Set the progress of the task.
     *
     * @param progress
     *            Progress value.
     */
    public void setProgress(final int progress) {
        
        SwingUtilities.invokeLater(new Runnable() {
            
            public void run() {
                
                progressBar.setValue(progress);
            }
        });
        
    }
    
    /**
     * Get the progress of the task.
     *
     * @return Progress value.
     */
    public int getProgress() {
        
        return progressBar.getValue();
    }
    
    /**
     * Set the dialog window message.
     *
     * @param message
     *            Message
     */
    public void setMessage(final String message) {
        
        SwingUtilities.invokeLater(new Runnable() {
            
            public void run() {
                
                messageLabel.setText(message);
            }
        });
        
    }
    
    /**
     * Get the dialog window message.
     *
     * @return Message
     */
    public String getMessage() {
        
        return messageLabel.getText();
    }
    
    /**
     * {@inheritDoc}
     */
    public void start() {
        
        // Show progress dialog.
        SwingUtilities.invokeLater(new Runnable() {
            
            public void run() {
                getDialog().setVisible(true);
            }
        });
        
        // Disposing thread.
        Thread thread = new Thread(new Runnable() {
            
            public void run() {
                
                try {
                    ProgressBarThread.this.join();
                } catch (InterruptedException exception) {
                }
                
                // Dispose progress dialog.
                SwingUtilities.invokeLater(new Runnable() {
                    
                    public void run() {
                        getDialog().dispose();
                    }
                });
            }
        });
        thread.start();
        
        super.start();
    }
}